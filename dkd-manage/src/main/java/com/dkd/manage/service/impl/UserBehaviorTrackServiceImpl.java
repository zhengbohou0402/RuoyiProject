package com.dkd.manage.service.impl;

import com.dkd.common.annotation.DataSource;
import com.dkd.common.enums.DataSourceType;
import com.dkd.common.exception.ServiceException;
import com.dkd.common.utils.StringUtils;
import com.dkd.framework.datasource.DynamicDataSourceContextHolder;
import com.dkd.manage.domain.dto.UserBehaviorTrackQueryDTO;
import com.dkd.manage.domain.vo.TbUserBehaviorTrackVo;
import com.dkd.manage.mapper.UserBehaviorTrackMapper;
import com.dkd.manage.mapper.UserBehaviorWhitelistMapper;
import com.dkd.manage.service.IUserBehaviorTrackService;
import com.github.pagehelper.PageHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


// todo： 缓存不一致，查询时间间隔20分钟

/**
 * 用户行为轨迹查询 Service 实现
 * 
 * =====================================================
 * 缓存架构设计说明（面试重点💡）
 * =====================================================
 * 
 * 一、业务背景：
 * ClickHouse 查询用户行为轨迹数据，单次查询耗时较长（500ms-2s）
 * 用户频繁翻页查询同一时间段数据，导致重复查询压力大
 * 
 * 二、缓存方案演进：
 * 
 * 【方案1】Caffeine 本地缓存（初版）
 *   - 实现：JVM 堆内存缓存
 *   - 优势：查询速度极快（纳秒级），无网络开销
 *   - 劣势：
 *     1. 单机缓存，集群环境缓存不共享，每个实例都要查一次 ClickHouse
 *     2. 受 JVM 堆内存限制，缓存容量有限
 *     3. 服务重启后缓存全部丢失
 *   - 适用场景：单机部署、小数据量
 * 
 * 【方案2】Redis 分布式缓存（升级版）⭐推荐
 *   - 实现：Redis 存储全量查询结果，内存分页
 *   - 优势：
 *     1. 集群环境多实例共享缓存，大幅减少 ClickHouse 查询次数
 *     2. 缓存容量大，可存储更多数据
 *     3. 缓存持久化，服务重启后缓存仍有效
 *     4. 支持分布式锁，防止缓存击穿
 *   - 劣势：有网络 IO 开销（但远小于 ClickHouse 查询）
 *   - 适用场景：生产环境、分布式部署
 * 
 * 【方案3】多级缓存架构（终极版）⭐⭐最优
 *   - 实现：Redis（L1） + Caffeine（L2）双重缓存
 *   - 查询流程：Caffeine → Redis → ClickHouse
 *   - 优势：
 *     1. Caffeine 作为一级缓存，响应速度最快
 *     2. Redis 作为二级缓存，集群共享
 *     3. Redis 故障时自动降级到 Caffeine
 *     4. 综合两种方案的优势
 * 
 * 三、缓存策略：
 *   - 缓存粒度：按 手机号+时间范围 缓存全量数据
 *   - 分页方式：内存分页（List.subList）
 *   - 过期时间：5分钟（可配置）
 *   - Key规则：user:behavior:track:{phone}_{startTime}_{endTime}
 * 
 * 四、面试要点：
 *   1. 为什么不用 PageHelper 分页？
 *      → ClickHouse 每次分页都要重新查询，缓存全量数据后内存分页更快
 *   2. 为什么要双重缓存？
 *      → 提高可用性，Redis 故障时不影响业务
 *   3. 如何防止缓存穿透/击穿？
 *      → 白名单前置校验、空结果也缓存、可加分布式锁
 *   4. 缓存一致性如何保证？
 *      → 业务场景为查询历史数据，无需强一致性，过期时间足够
 */
@Service
public class UserBehaviorTrackServiceImpl implements IUserBehaviorTrackService {

    private static final Logger log = LoggerFactory.getLogger(UserBehaviorTrackServiceImpl.class);

    // ===================== 缓存配置常量 =====================
    
    /**
     * Redis缓存Key前缀
     */
    private static final String REDIS_CACHE_KEY_PREFIX = "user:behavior:track:";

    /**
     * Redis缓存过期时间（秒）
     */
    private static final long REDIS_CACHE_EXPIRE_SECONDS = 300; // 5分钟

    /**
     * Caffeine 本地缓存最大容量
     */
    private static final int LOCAL_CACHE_MAX_SIZE = 1000;

    /**
     * Caffeine 本地缓存过期时间（分钟）
     */
    private static final int LOCAL_CACHE_EXPIRE_MINUTES = 20;

    // ===================== 配置开关 =====================
    
    /**
     * 是否启用 Redis 缓存（通过配置文件控制，默认启用）
     * 配置项：user.behavior.cache.redis.enabled
     * 
     * 面试说明：提供开关方便灰度发布和问题回滚
     */
    @Value("${user.behavior.cache.redis.enabled:true}")
    private boolean redisEnabled;

    // ===================== 缓存组件注入 =====================

    /**
     * Redis 分布式缓存（推荐方案）
     * 注意：required = false 允许 Redis 不存在时自动降级到本地缓存
     */
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Caffeine 本地缓存（备用方案/降级方案）
     * 保留原有实现，作为 Redis 的降级方案
     */
    private Cache<String, List<TbUserBehaviorTrackVo>> localCache;

    // ===================== Mapper 注入 =====================

    @Autowired
    private UserBehaviorTrackMapper userBehaviorTrackMapper;

    @Autowired
    private UserBehaviorWhitelistMapper userBehaviorWhitelistMapper;

    // ===================== 初始化 =====================

    /**
     * 初始化缓存组件
     */
    @PostConstruct
    public void init() {
        // 初始化 Caffeine 本地缓存（始终启用，作为备用方案）
        localCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(LOCAL_CACHE_EXPIRE_MINUTES))
                .maximumSize(LOCAL_CACHE_MAX_SIZE)
                .build();
        
        // 打印缓存配置信息
        boolean redisAvailable = redisEnabled && redisTemplate != null;
        log.info("========================================");
        log.info("用户行为轨迹缓存初始化完成");
        log.info("Redis缓存: {}", redisAvailable ? "✅ 已启用" : "❌ 未启用（已降级到本地缓存）");
        log.info("Caffeine本地缓存: ✅ 已启用（最大{}条，{}分钟过期）", LOCAL_CACHE_MAX_SIZE, LOCAL_CACHE_EXPIRE_MINUTES);
        log.info("缓存策略: {}", redisAvailable ? "多级缓存（Redis + Caffeine）" : "单级缓存（仅Caffeine）");
        log.info("========================================");
    }

    // ===================== 业务方法 =====================

    /**
     * 分页查询（升级版：支持 Redis 分布式缓存 + Caffeine 本地缓存多级架构）
     *
     * 查询流程：
     * 1. 白名单校验（MySQL）
     * 2. Caffeine 本地缓存查询（L2缓存）
     * 3. Redis 缓存查询（L1缓存）
     * 4. ClickHouse 数据库查询
     * 5. 写入双重缓存
     * 6. 内存分页 + 脱敏返回
     *
     * @param query 查询入参
     * @return 返回结果
     */
    @Override
    public List<TbUserBehaviorTrackVo> selectUserBehaviorTrackList(UserBehaviorTrackQueryDTO query) {
        // ===== 第一步：参数校验 =====
        validatePhone(query);
        validateTimeRange(query);
        
        // ===== 第二步：白名单校验（MySQL，前置条件）===== 数据库没改，不查了
        checkWhiteList(query.getPhone());

        // ===== 第三步：生成缓存Key =====
        String cacheKey = buildCacheKey(query.getPhone(), query.getBeginTime().getTime(), query.getEndTime().getTime());

        // ===== 第四步：多级缓存查询 =====
        List<TbUserBehaviorTrackVo> allList = getFromCache(cacheKey, query);

        // ===== 第五步：内存分页 =====
        List<TbUserBehaviorTrackVo> pageList = memoryPagination(allList, query.getPageNum(), query.getPageSize());

        // ===== 第六步：手机号脱敏 =====
        for (TbUserBehaviorTrackVo vo : pageList) {
            vo.setUserId(maskPhone(vo.getUserId()));
        }

        return pageList;
    }

    /**
     * 导出不分页
     *
     * @param query 查询入参
     * @return 返回结果
     */
    @Override
    public List<TbUserBehaviorTrackVo> selectUserBehaviorTrackAll(UserBehaviorTrackQueryDTO query) {
        validatePhone(query);
        validateTimeRange(query);
        // 导出时强制校验白名单（根据业务需求调整）
        checkWhiteList(query.getPhone());

        // ===== 生成缓存Key =====
        String cacheKey = buildCacheKey(query.getPhone(), query.getBeginTime().getTime(), query.getEndTime().getTime());
        if(getFromRedis(cacheKey) != null) {
            return getFromRedis(cacheKey);
        }
        // 清除分页，查询全量
        PageHelper.clearPage();
        query.setPageNum(null);
        query.setPageSize(null);
        
        // 使用 ClickHouse 数据源执行查询
        List<TbUserBehaviorTrackVo> allList = executeWithClickHouse(
                () -> userBehaviorTrackMapper.selectUserBehaviorTrackList(query));

        // 写入 Redis 缓存
        if (allList != null && !allList.isEmpty()) {
            putToRedis(cacheKey, allList);
        } else {
            // 空结果也缓存，防止缓存穿透
            allList = Collections.emptyList();
            putToRedis(cacheKey, allList);
        }

        // 手机号脱敏
        for (TbUserBehaviorTrackVo vo : allList) {
            vo.setUserId(maskPhone(vo.getUserId()));
        }

        return allList;
    }

    /**
     * 统计总数
     */
    @Override
    @DataSource(DataSourceType.CLICKHOUSE)
    public Long countUserBehaviorTrack(UserBehaviorTrackQueryDTO query) {
        PageHelper.clearPage();
        return userBehaviorTrackMapper.countUserBehaviorTrack(query);
    }

    // ===================== 多级缓存核心方法 =====================

    /**
     * 多级缓存查询（核心方法）
     * 
     * 查询顺序：Caffeine → Redis → ClickHouse
     * 
     * @param cacheKey 缓存Key
     * @param query 查询条件（用于缓存未命中时查询数据库）
     * @return 查询结果
     */
    private List<TbUserBehaviorTrackVo> getFromCache(String cacheKey, UserBehaviorTrackQueryDTO query) {
        List<TbUserBehaviorTrackVo> allList = null;

        // ===== L2缓存：Caffeine 本地缓存（速度最快）=====
        allList = localCache.getIfPresent(cacheKey);
        if (allList != null) {
            log.debug("✅ Caffeine本地缓存命中: cacheKey={}, size={}", cacheKey, allList.size());
            return allList;
        }
        log.debug("❌ Caffeine本地缓存未命中: cacheKey={}", cacheKey);

        // ===== L1缓存：Redis 分布式缓存 =====
        if (isRedisAvailable()) {
            allList = getFromRedis(cacheKey);
            if (allList != null) {
                log.info("✅ Redis缓存命中: cacheKey={}, size={}", cacheKey, allList.size());
                // 回写到 Caffeine 本地缓存
                localCache.put(cacheKey, allList);
                return allList;
            }
            log.debug("❌ Redis缓存未命中: cacheKey={}", cacheKey);
        }

        // ===== 缓存全部未命中，查询 ClickHouse =====
        log.info("🔍 缓存全部未命中，查询ClickHouse: phone={}, timeRange=[{} ~ {}]", 
                query.getPhone(), query.getBeginTime(), query.getEndTime());
        
        allList = queryFromClickHouse(query);

        // ===== 写入双重缓存 =====
        if (allList != null && !allList.isEmpty()) {
            // 写入 Redis（优先）
            if (isRedisAvailable()) {
                putToRedis(cacheKey, allList);
            }
            // 写入 Caffeine 本地缓存（备用）
            localCache.put(cacheKey, allList);
            log.info("💾 ClickHouse查询完成，已写入双重缓存: size={}", allList.size());
        } else {
            allList = Collections.emptyList();
            log.warn("⚠️  ClickHouse查询结果为空: phone={}", query.getPhone());
        }

        return allList;
    }

    /**
     * 从 Redis 获取缓存
     */
    @SuppressWarnings("unchecked")
    private List<TbUserBehaviorTrackVo> getFromRedis(String cacheKey) {
        try {
            String redisKey = REDIS_CACHE_KEY_PREFIX + cacheKey;
            Object cacheObj = redisTemplate.opsForValue().get(redisKey);
            if (cacheObj instanceof List) {
                return (List<TbUserBehaviorTrackVo>) cacheObj;
            }
        } catch (Exception e) {
            log.error("❌ Redis查询异常，自动降级到本地缓存: cacheKey={}, error={}", cacheKey, e.getMessage());
        }
        return null;
    }

    /**
     * 写入 Redis 缓存
     */
    private void putToRedis(String cacheKey, List<TbUserBehaviorTrackVo> data) {
        try {
            String redisKey = REDIS_CACHE_KEY_PREFIX + cacheKey;
            redisTemplate.opsForValue().set(redisKey, data, REDIS_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.debug("💾 写入Redis缓存成功: key={}, size={}, expire={}s", redisKey, data.size(), REDIS_CACHE_EXPIRE_SECONDS);
        } catch (Exception e) {
            log.error("❌ Redis写入异常: cacheKey={}, error={}", cacheKey, e.getMessage());
        }
    }

    /**
     * 判断 Redis 是否可用
     */
    private boolean isRedisAvailable() {
        return redisEnabled && redisTemplate != null;
    }

    /**
     * 从 ClickHouse 查询全量数据
     */
    private List<TbUserBehaviorTrackVo> queryFromClickHouse(UserBehaviorTrackQueryDTO query) {
        // 关闭 PageHelper 分页，查询全量数据
        PageHelper.clearPage();
        Integer originalPageNum = query.getPageNum();
        Integer originalPageSize = query.getPageSize();
        query.setPageNum(null);
        query.setPageSize(null);

        try {
            List<TbUserBehaviorTrackVo> list = executeWithClickHouse(
                    () -> userBehaviorTrackMapper.selectUserBehaviorTrackList(query));
            return list != null ? list : Collections.emptyList();
        } finally {
            // 恢复分页参数
            query.setPageNum(originalPageNum);
            query.setPageSize(originalPageSize);
        }
    }

    /**
     * 生成缓存Key
     * 
     * 格式：{phone}_{startTime}_{endTime}
     * 示例：13800138000_1673452800000_1673539200000
     */
    private String buildCacheKey(String phone, Long startTime, Long endTime) {
        return phone + "_" + startTime + "_" + endTime;
    }

    // ===================== 内存分页 =====================

    /**
     * 内存分页
     * 
     * 为什么不用 PageHelper？
     * → ClickHouse 每次分页都要重新查询全表，性能差
     * → 缓存全量数据后，内存分页更快
     * 
     * @param allList 全量数据
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    private List<TbUserBehaviorTrackVo> memoryPagination(List<TbUserBehaviorTrackVo> allList, 
                                                         Integer pageNum, 
                                                         Integer pageSize) {
        if (allList == null || allList.isEmpty()) {
            return Collections.emptyList();
        }

        // 默认值处理
        int currentPage = pageNum == null ? 1 : pageNum;
        int size = pageSize == null ? 10 : pageSize;

        // 计算分页范围
        int fromIndex = (currentPage - 1) * size;
        int toIndex = Math.min(fromIndex + size, allList.size());

        // 越界检查
        if (fromIndex >= allList.size() || fromIndex < 0) {
            return Collections.emptyList();
        }

        // 截取分页数据
        return new ArrayList<>(allList.subList(fromIndex, toIndex));
    }

    // ===================== 数据源切换 =====================

    /**
     * 使用 ClickHouse 数据源执行操作
     * 
     * 解决问题：同一方法中需要混合使用 MySQL（白名单）和 ClickHouse（行为轨迹）
     * 
     * @param supplier 需要执行的操作
     * @return 操作结果
     */
    private <T> T executeWithClickHouse(Supplier<T> supplier) {
        try {
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.CLICKHOUSE.name());
            return supplier.get();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    // ===================== 数据处理工具方法 =====================

    /**
     * 手机号脱敏
     * 
     * 规则：保留前3位和后4位，中间用****替换
     * 示例：13812345678 → 138****5678
     */
    private String maskPhone(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    // ===================== 参数校验方法 =====================

    /**
     * 校验手机号
     */
    private void validatePhone(UserBehaviorTrackQueryDTO query) {
        if (query == null || StringUtils.isBlank(query.getPhone())) {
            throw new ServiceException("手机号码不能为空");
        }
    }

    /**
     * 校验时间范围
     */
    private void validateTimeRange(UserBehaviorTrackQueryDTO query) {
        if (query.getBeginTime() == null) {
            throw new ServiceException("开始时间不能为空");
        }
        if (query.getEndTime() == null) {
            throw new ServiceException("结束时间不能为空");
        }
        if (query.getBeginTime().after(query.getEndTime())) {
            throw new ServiceException("开始时间不能晚于结束时间");
        }
    }

    /**
     * 白名单校验
     * 
     * 注意：使用默认数据源（MySQL），在切换到 ClickHouse 之前执行
     */
    private void checkWhiteList(String phone) {
        int cnt = userBehaviorWhitelistMapper.countByPhone(phone);
        if (cnt <= 0) {
            throw new ServiceException("请将手机号码添加至白名单");
        }
    }

    // ===================== 纯 Caffeine 缓存实现（新增方法）=====================

    /**
     * 分页查询（纯 Caffeine 本地缓存版本）
     * 
     * =====================================================
     * 单级缓存架构设计说明 💡
     * =====================================================
     * 
     * 一、技术选型：
     *   - 只使用 Caffeine 本地缓存（JVM 堆内存）
     *   - 不依赖 Redis 等外部组件
     * 
     * 二、优势：
     *   1. 查询速度极快：纳秒级别响应，无网络 IO 开销
     *   2. 部署简单：无需额外中间件，降低运维成本
     *   3. 内存占用可控：通过 LRU 淘汰策略，自动清理冷数据
     *   4. 高可用性：无外部依赖故障风险
     * 
     * 三、劣势：
     *   1. 单机缓存：集群环境下每个实例需独立查询一次
     *   2. 容量受限：受 JVM 堆内存限制
     *   3. 缓存不共享：集群情况下缓存利用率低
     *   4. 无持久化：服务重启缓存全部丢失
     * 
     * 四、适用场景：
     *   - 单机部署或小规模集群
     *   - 对缓存一致性要求不高的查询场景
     *   - 希望简化架构、降低运维成本的项目
     * 
     * 五、查询流程：
     *   1. 白名单校验（MySQL）
     *   2. 生成缓存 Key（手机号+时间范围）
     *   3. Caffeine 缓存查询
     *   4. 缓存未命中 → ClickHouse 查询全量数据
     *   5. 写入 Caffeine 缓存
     *   6. 内存分页 + 脱敏返回
     * 
     * 六、缓存策略：
     *   - 缓存粒度：按 手机号+时间范围 缓存全量数据
     *   - 过期策略：写入后 20 分钟自动过期
     *   - 淘汰策略：LRU（最近最少使用），最多缓存 1000 条
     *   - 分页方式：内存分页（List.subList）
     * 
     * 七、面试要点：
     *   Q1：为什么不每次都查 ClickHouse？
     *   → ClickHouse 查询慢（500ms-2s），用户频繁翻页会造成大量重复查询
     *   
     *   Q2：为什么不用 PageHelper 分页？
     *   → ClickHouse 每次分页都要全表扫描，缓存全量数据后内存分页更快
     *   
     *   Q3：缓存会不会把内存撑爆？
     *   → Caffeine 有最大容量限制（1000条），超出后自动 LRU 淘汰
     *   
     *   Q4：如何防止缓存穿透？
     *   → 白名单前置校验 + 空结果也缓存（Collections.emptyList）
     *   
     *   Q5：集群环境怎么办？
     *   → 升级到 Redis 分布式缓存（参见原方法 selectUserBehaviorTrackList）
     * 
     * @param query 查询入参
     * @return 返回结果
     */
    @Override
    public List<TbUserBehaviorTrackVo> selectUserBehaviorTrackListWithCaffeineOnly(UserBehaviorTrackQueryDTO query) {
        // ===== 第一步：参数校验 =====
        validatePhone(query);
        validateTimeRange(query);
        
        // ===== 第二步：白名单校验（MySQL，前置条件）=====
        checkWhiteList(query.getPhone());

        // ===== 第三步：生成缓存Key =====
        String cacheKey = buildCacheKey(query.getPhone(), query.getBeginTime().getTime(), query.getEndTime().getTime());

        // ===== 第四步：Caffeine 缓存查询 =====
        List<TbUserBehaviorTrackVo> allList = localCache.getIfPresent(cacheKey);
        
        if (allList != null) {
            // 缓存命中
            log.info("✅ Caffeine缓存命中: phone={}, cacheKey={}, size={}", 
                    query.getPhone(), cacheKey, allList.size());
        } else {
            // 缓存未命中，查询 ClickHouse
            log.info("❌ Caffeine缓存未命中，查询ClickHouse: phone={}, timeRange=[{} ~ {}]", 
                    query.getPhone(), query.getBeginTime(), query.getEndTime());
            
            allList = queryFromClickHouse(query);
            
            // 写入 Caffeine 缓存（即使为空也缓存，防止缓存穿透）
            if (allList != null && !allList.isEmpty()) {
                localCache.put(cacheKey, allList);
                log.info("💾 ClickHouse查询完成，已写入Caffeine缓存: size={}, cacheKey={}", 
                        allList.size(), cacheKey);
            } else {
                allList = Collections.emptyList();
                localCache.put(cacheKey, allList);
                log.warn("⚠️  ClickHouse查询结果为空，已缓存空结果: phone={}", query.getPhone());
            }
        }

        // ===== 第五步：内存分页 =====
        List<TbUserBehaviorTrackVo> pageList = memoryPagination(allList, query.getPageNum(), query.getPageSize());

        // ===== 第六步：手机号脱敏 =====
        for (TbUserBehaviorTrackVo vo : pageList) {
            vo.setUserId(maskPhone(vo.getUserId()));
        }

        log.info("📄 返回分页数据: phone={}, pageNum={}, pageSize={}, 返回条数={}", 
                query.getPhone(), query.getPageNum(), query.getPageSize(), pageList.size());

        return pageList;
    }

    // ===================== 纯 Redis 缓存实现（新增方法）=====================

    /**
     * 分页查询（纯 Redis 分布式缓存版本）
     * 
     * =====================================================
     * Redis 单级缓存架构设计说明 💡
     * =====================================================
     * 
     * 一、技术选型：
     *   - 只使用 Redis 分布式缓存
     *   - 不使用本地缓存（Caffeine）
     * 
     * 二、优势：
     *   1. 缓存共享：集群环境下多实例共享同一份缓存，减少 ClickHouse 查询次数
     *   2. 容量大：Redis 内存不受 JVM 堆限制，可存储更多数据
     *   3. 可持久化：Redis 支持 RDB/AOF 持久化，服务重启缓存仍有效
     *   4. 支持分布式锁：可防止缓存击穿（本示例未实现，可扩展）
     *   5. 过期策略灵活：支持设置精确的过期时间
     * 
     * 三、劣势：
     *   1. 网络开销：每次查询需经过网络 IO（但远小于 ClickHouse 查询耗时）
     *   2. 依赖外部服务：Redis 故障会影响缓存功能
     *   3. 序列化开销：对象需要序列化/反序列化
     * 
     * 四、适用场景：
     *   - 分布式/集群部署环境
     *   - 对缓存一致性有要求的场景
     *   - 需要缓存共享的多实例应用
     *   - 数据量较大，本地缓存容量不足的场景
     * 
     * 五、查询流程：
     *   1. 参数校验
     *   2. 白名单校验（MySQL，前置条件）
     *   3. 生成缓存 Key（手机号+时间范围）
     *   4. Redis 缓存查询
     *   5. 缓存未命中 → ClickHouse 查询全量数据
     *   6. 写入 Redis 缓存（设置过期时间）
     *   7. 内存分页 + 脱敏返回
     * 
     * 六、缓存策略：
     *   - 缓存粒度：按 手机号+时间范围 缓存全量数据
     *   - 过期策略：写入后 5 分钟自动过期（可配置）
     *   - Key格式：user:behavior:track:{phone}_{startTime}_{endTime}
     *   - 分页方式：内存分页（List.subList）
     * 
     * 七、面试要点：
     *   Q1：为什么选择 Redis 而不是本地缓存？
     *   → 集群环境需要缓存共享，避免每个实例都查一次 ClickHouse
     *   
     *   Q2：Redis 网络开销会不会很大？
     *   → 局域网内 Redis 响应通常在 1-5ms，远小于 ClickHouse 的 500ms-2s
     *   
     *   Q3：如何防止缓存击穿？
     *   → 可以使用 Redis 分布式锁（Redisson），保证只有一个请求查询数据库
     *   
     *   Q4：Redis 挂了怎么办？
     *   → 方案1：降级直接查询 ClickHouse
     *   → 方案2：升级到多级缓存架构（Redis + Caffeine）
     *   
     *   Q5：缓存数据一致性如何保证？
     *   → 行为轨迹是历史数据，无需强一致性，5分钟过期时间足够
     * 
     * @param query 查询入参
     * @return 返回结果
     */
    @Override
    public List<TbUserBehaviorTrackVo> selectUserBehaviorTrackListWithRedisOnly(UserBehaviorTrackQueryDTO query) {
        // ===== 第一步：参数校验 =====
        validatePhone(query);
        validateTimeRange(query);
        
        // ===== 第二步：白名单校验（MySQL，前置条件）=====
        checkWhiteList(query.getPhone());

        // ===== 第三步：检查 Redis 是否可用 =====
        if (!isRedisAvailable()) {
            log.error("❌ Redis不可用，无法执行纯Redis缓存查询");
            throw new ServiceException("Redis服务不可用，请稍后重试");
        }

        // ===== 第四步：生成缓存Key =====
        String cacheKey = buildCacheKey(query.getPhone(), query.getBeginTime().getTime(), query.getEndTime().getTime());
        String redisKey = REDIS_CACHE_KEY_PREFIX + cacheKey;

        // ===== 第五步：Redis 缓存查询 =====
        List<TbUserBehaviorTrackVo> allList = null;
        
        try {
            allList = getFromRedis(cacheKey);
        } catch (Exception e) {
            log.error("❌ Redis查询异常: key={}, error={}", redisKey, e.getMessage());
            throw new ServiceException("Redis查询异常，请稍后重试");
        }
        
        if (allList != null) {
            // 缓存命中
            log.info("✅ Redis缓存命中: phone={}, redisKey={}, size={}", 
                    query.getPhone(), redisKey, allList.size());
        } else {
            // 缓存未命中，查询 ClickHouse
            log.info("❌ Redis缓存未命中，查询ClickHouse: phone={}, timeRange=[{} ~ {}]", 
                    query.getPhone(), query.getBeginTime(), query.getEndTime());
            
            allList = queryFromClickHouse(query);
            
            // 写入 Redis 缓存
            if (allList != null && !allList.isEmpty()) {
                putToRedis(cacheKey, allList);
                log.info("💾 ClickHouse查询完成，已写入Redis缓存: size={}, redisKey={}, expire={}s", 
                        allList.size(), redisKey, REDIS_CACHE_EXPIRE_SECONDS);
            } else {
                // 空结果也缓存，防止缓存穿透
                allList = Collections.emptyList();
                putToRedis(cacheKey, allList);
                log.warn("⚠️  ClickHouse查询结果为空，已缓存空结果: phone={}", query.getPhone());
            }
        }

        // ===== 第六步：内存分页 =====
        List<TbUserBehaviorTrackVo> pageList = memoryPagination(allList, query.getPageNum(), query.getPageSize());

        // ===== 第七步：手机号脱敏 =====
        for (TbUserBehaviorTrackVo vo : pageList) {
            vo.setUserId(maskPhone(vo.getUserId()));
        }

        log.info("📄 返回分页数据: phone={}, pageNum={}, pageSize={}, 返回条数={}",
                query.getPhone(), query.getPageNum(), query.getPageSize(), pageList.size());

        return pageList;
    }

    // ===================== 问题复现：PageHelper 双 ORDER BY =====================

    /**
     * 【问题复现用】走 RuoYi 通用分页 {@link com.dkd.common.utils.PageUtils#startPage()}。
     *
     * 复现步骤：
     * 1. 请求带上 orderByColumn（如 daytime）和 isAsc（如 asc）参数
     * 2. {@link com.dkd.common.core.page.PageDomain#getOrderBy()} 会拼出 "daytime asc"
     * 3. {@link com.dkd.common.utils.PageUtils#startPage()} 内部调用
     *    PageHelper.startPage(pageNum, pageSize, "daytime asc")
     *    —— 这个重载只会在最终 SQL 末尾追加 " order by daytime asc"，不会解析/替换已有 ORDER BY
     * 4. 而 UserBehaviorTrackMapper.xml 的 selectUserBehaviorTrackList 里已经写死了
     *    "ORDER BY daytime DESC"
     * 5. 两者拼接后，最终发往 ClickHouse 的 SQL 变成：
     *    ... ORDER BY daytime DESC LIMIT ... order by daytime asc
     *    双 ORDER BY 关键字，ClickHouse 直接报语法错误
     *
     * 不做白名单/缓存这些生产逻辑，只做最小复现。
     */
    @Override
    public List<TbUserBehaviorTrackVo> demoPageHelperOrderByBug(UserBehaviorTrackQueryDTO query) {
        validatePhone(query);
        validateTimeRange(query);

        // 使用 RuoYi 通用分页：会从当前请求里读取 orderByColumn / isAsc 并拼进 PageHelper.startPage 的第三个参数
        com.dkd.common.utils.PageUtils.startPage();

        log.warn("⚠️ [DEMO] 即将以 RuoYi 通用分页方式查询 ClickHouse，若请求携带 orderByColumn 参数，预期会触发双 ORDER BY SQL 异常");

        return executeWithClickHouse(() -> userBehaviorTrackMapper.selectUserBehaviorTrackList(query));
    }

    // ===================== 模拟长连接：增量轮询查询 =====================

    /**
     * 【模拟长连接-增量轮询用】
     * 与 UserBehaviorLogServiceImpl#selectIncrementalData 思路一致（增量轮询代替 WebSocket），
     * 但下次轮询基准的取法不同：UserBehaviorLog 那边用 MySQL 的 MAX(event_time) 是可靠的
     * （MySQL 空结果集返回 NULL，语义清晰）；这里改用服务端当前时间兜底，而不是
     * ClickHouse 的 MAX(daytime)。
     *
     * 面试踩坑点：本来也是照搬"用数据自身最大时间做下次基准"这个思路调用
     * ClickHouse 的 MAX(daytime)，但实测这个 JDBC 驱动读回来的 Date 跟真实写入
     * 瞬时值之间有一个稳定的 8 小时系统性偏差（驱动在 DateTime 读写两侧的时区处理
     * 不对称），如果拿这个偏差值做下次轮询的 afterTime，会导致每次轮询都把最近
     * 8 小时内的数据重新拉一遍，前端表格里全是重复行。
     * 由于这条链路是同步写入 ClickHouse（MergeTree 引擎写入后立即可查，没有
     * 显著的入库延迟），用服务端 new Date() 兜底足够安全，不需要为了防"入库延迟漏数据"
     * 这个边缘场景去依赖一个本身有偏差的驱动读取值。
     */
    @Override
    public Map<String, Object> selectTrackIncrementalData(String phone, Date queryTime) {
        checkWhiteList(phone);

        // 面试踩坑点：ClickHouse 的 daytime 列是 DateTime 类型（秒级精度，不支持毫秒）。
        // 如果绑定参数的 Date 带非零毫秒（比如直接用 epoch 毫秒数构造的 Date），
        // JDBC 驱动会把它序列化成带 ".xxx" 毫秒后缀的字符串传给 ClickHouse，
        // 而 DateTime 类型无法解析这种带小数的字符串，直接报
        // "Cannot convert string ... to type DateTime" 语法错误。
        // 所以传入 WHERE 条件之前要先把毫秒清零（向下取整到秒）。
        Date queryTimeAtSecond = queryTime != null ? new Date((queryTime.getTime() / 1000) * 1000) : null;

        List<TbUserBehaviorTrackVo> list = executeWithClickHouse(
                () -> userBehaviorTrackMapper.selectIncrementalTrack(phone, queryTimeAtSecond));

        // 下次轮询基准：用服务端当前时间（向下取整到秒），不依赖 ClickHouse 的 MAX(daytime) 读取值
        Date nextQueryTime = new Date((System.currentTimeMillis() / 1000) * 1000);

        if (list != null) {
            for (TbUserBehaviorTrackVo vo : list) {
                vo.setUserId(maskPhone(vo.getUserId()));
            }
        }

        // 面试踩坑点：这里故意返回 epoch 毫秒数（Long），而不是格式化后的日期字符串。
        // 实测 ClickHouse JDBC 驱动读回 daytime 列时，转换出来的 java.util.Date
        // 跟写入时用 new Date() 得到的瞬时值之间存在 8 小时的系统性偏差（疑似驱动在
        // 读写两侧对 DateTime 的时区解释不对称）。如果用 SimpleDateFormat 把这个读回来的
        // 时间格式化成字符串再传给前端、前端下次轮询再把这个字符串传回来给后端重新 parse，
        // 就会在"格式化 -> 重新解析"这个环节被 JVM 本地时区二次误解释，导致下一轮查询的
        // afterTime 基准整体错位，出现"同一批数据被重复推送"的现象。
        // 用 epoch 毫秒做前后端交互协议，全程只做数值传递，不经过任何字符串格式化/解析，
        // 彻底规避这个时区坑。
        Map<String, Object> result = new HashMap<>();
        result.put("dataList", list == null ? Collections.emptyList() : list);
        result.put("queryEndTime", nextQueryTime.getTime());
        return result;
    }
}
