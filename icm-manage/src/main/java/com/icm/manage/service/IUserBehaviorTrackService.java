package com.icm.manage.service;

import com.icm.manage.domain.dto.UserBehaviorTrackQueryDTO;
import com.icm.manage.domain.vo.TbUserBehaviorTrackVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户行为轨迹查询 Service
 */
public interface IUserBehaviorTrackService {

    /**
     * 查询用户行为轨迹列表（含白名单前置校验）
     */
    List<TbUserBehaviorTrackVo> selectUserBehaviorTrackList(UserBehaviorTrackQueryDTO query);

    /**
     * 查询总数（用于分页）
     */
    Long countUserBehaviorTrack(UserBehaviorTrackQueryDTO query);

    /**
     * 查询用户行为轨迹（全量，不分页，专供导出使用）
     */
    List<TbUserBehaviorTrackVo> selectUserBehaviorTrackAll(UserBehaviorTrackQueryDTO query);

    /**
     * 分页查询（纯 Caffeine 本地缓存版本）
     * 
     * 适用场景：单机部署、小规模应用
     * 优势：查询速度极快、无外部依赖
     */
    List<TbUserBehaviorTrackVo> selectUserBehaviorTrackListWithCaffeineOnly(UserBehaviorTrackQueryDTO query);

    /**
     * 分页查询（纯 Redis 分布式缓存版本）
     *
     * 适用场景：分布式部署、多实例集群环境
     * 优势：缓存共享、容量大、可持久化
     */
    List<TbUserBehaviorTrackVo> selectUserBehaviorTrackListWithRedisOnly(UserBehaviorTrackQueryDTO query);

    /**
     * 【问题复现用】用 RuoYi 通用 PageHelper 分页（startPage）直接查询 ClickHouse。
     * 用于复现"Mapper SQL 里已写死 ORDER BY，再叠加 PageHelper.startPage(pageNum,pageSize,orderBy)
     * 注入的排序" 导致最终 SQL 出现两个 ORDER BY 而语法报错的问题。
     * 不供业务使用，仅面试/演示复现。
     */
    List<TbUserBehaviorTrackVo> demoPageHelperOrderByBug(UserBehaviorTrackQueryDTO query);

    /**
     * 【模拟长连接-增量轮询用】查询某手机号在 queryTime 之后新增的轨迹数据
     * 与 UserBehaviorLogServiceImpl#selectIncrementalData 是同一套设计思路，
     * 只是数据源换成了 ClickHouse：返回 dataList + queryEndTime（下次轮询基准时间）
     */
    Map<String, Object> selectTrackIncrementalData(String phone, Date queryTime);
}


