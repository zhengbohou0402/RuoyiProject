package com.icm.manage.controller;

import com.icm.common.core.controller.BaseController;
import com.icm.common.core.domain.AjaxResult;
import com.icm.manage.mapper.TestDataInsertMapper;
import com.icm.websocket.service.WebSocketService;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试数据生成器Controller（专业版）
 * 
 * 【核心特性】
 * 1. 线程安全 - ReentrantLock 保证并发安全
 * 2. 优雅停止 - CountDownLatch 等待线程结束
 * 3. 可配置 - 支持自定义间隔、批量插入
 * 4. 状态监控 - 实时查看运行状态和统计
 * 5. 资源管理 - @PreDestroy 确保应用关闭时释放资源
 * 
 * 【技术亮点】
 * - ScheduledExecutorService 定时任务线程池
 * - AtomicLong 无锁计数器，高性能统计
 * - volatile 保证多线程可见性
 * - awaitTermination 优雅关闭线程池
 * - TPS 实时统计
 * 
 * @author icm
 * @date 2026-01-29
 */
@RestController
@RequestMapping("/manage/testDataGenerator")
public class TestDataGeneratorController extends BaseController
{
    @Autowired
    private TestDataInsertMapper testDataInsertMapper;

    @Autowired
    private WebSocketService webSocketService;

    /** WebSocket 业务推送场景：实时数据验证 */
    private static final String WS_SCENE_BEHAVIOR_LOG = "behaviorLog";

    /**
     * 内容按 5 档循环，长度依次递增，方便肉眼看出每条数据的差异
     * （事件类型、平台、浏览器、域名、IP、UA、SDK版本、数据源ID、属性JSON全部跟着变）
     */
    private static final String[] EVENT_KEYS = {"pv", "click", "scroll", "submit", "imp"};
    private static final String[] PLATFORMS = {"web", "app", "h5", "wechat-mini", "alipay-mini"};
    private static final String[] BROWSERS = {
            "Chrome",
            "Safari Mobile",
            "Chrome Mobile 141",
            "WeChat Built-in Browser 8.0",
            "Alipay Built-in Browser MiniProgram WebView"
    };
    private static final String[] DOMAINS = {
            "m.10086.cn",
            "dev.coc.10086.cn",
            "shop.coc.10086.cn",
            "activity.coc.10086.cn",
            "user-center.coc.10086.cn"
    };
    private static final String[] IPS = {
            "10.0.0.1", "192.168.1.100", "172.16.5.23", "39.183.168.62", "117.136.12.79"
    };
    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0)",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)",
            "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0",
            "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/99.0.4844.88 Mobile Safari/537.36 MicroMessenger/8.0.20",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 MobileSafari/604.1 AlipayClient/10.3.10.9153 Nebula AlipayDefined(nt:WIFI,ws:414|736|3) AliApp(AP/10.3.10) AlipayClient/10.3.10.9153 Language/zh-Hans"
    };
    private static final String[] SDK_VERSIONS = {"1.0.0", "1.2.0", "2.0.0-beta", "3.8.6", "3.8.6-hotfix.1"};
    private static final String[] DATA_SOURCE_IDS = {"a1", "b22", "c333", "d4444", "e55555"};
    private static final String[] ATTRIBUTES = {
            "{\"$path\":\"/home\",\"$title\":\"首页\"}",
            "{\"$path\":\"/product/detail\",\"$title\":\"商品详情页\",\"product_id\":\"12345\"}",
            "{\"$path\":\"/cart/checkout\",\"$title\":\"购物车结算页\",\"items\":[{\"sku\":\"A001\",\"qty\":2},{\"sku\":\"B002\",\"qty\":1}]}",
            "{\"$path\":\"/order/confirm/pay\",\"$title\":\"订单确认支付详情页\",\"order_id\":\"ORD20260703001\",\"amount\":299.00,\"pay_method\":\"alipay\"}",
            "{\"$path\":\"/user/profile/settings/manage\",\"$title\":\"用户个人中心设置管理页面\",\"user_id\":\"U1000234\",\"settings\":{\"notify\":true,\"theme\":\"dark\",\"lang\":\"zh-CN\",\"privacy_level\":\"high\"}}"
    };

    /**
     * 按 id 对 5 取模插入一条内容可变长的测试数据
     */
    private int insertRichData(Long id, String userMobile, Date now) {
        int idx = (int) (id % 5);
        return testDataInsertMapper.insertTestDataRich(
                id, userMobile, now,
                EVENT_KEYS[idx], PLATFORMS[idx], BROWSERS[idx], DOMAINS[idx], IPS[idx],
                USER_AGENTS[idx], SDK_VERSIONS[idx], DATA_SOURCE_IDS[idx], ATTRIBUTES[idx]
        );
    }

    /**
     * 【长链接推送】将刚插入的一条行为日志实时推送给订阅该手机号的 WebSocket 客户端。
     * 无订阅连接时静默跳过，不影响生成器主流程。
     */
    private void pushToWebSocket(String userMobile, Long id, Date now) {
        try {
            int idx = (int) (id % 5);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", id);
            data.put("userMobile", userMobile);
            data.put("eventKey", EVENT_KEYS[idx]);
            data.put("eventType", "CUSTOM_EVENT");
            data.put("eventTime", now);
            data.put("clientTime", now);
            data.put("platform", PLATFORMS[idx]);
            data.put("browser", BROWSERS[idx]);
            data.put("domain", DOMAINS[idx]);
            data.put("ip", IPS[idx]);
            data.put("sdkVersion", SDK_VERSIONS[idx]);
            data.put("dataSourceId", DATA_SOURCE_IDS[idx]);
            data.put("attributes", ATTRIBUTES[idx]);
            String json = JSON.toJSONString(data);
            // 推送失败（无订阅者）属于正常情况，不打错误日志
            webSocketService.pushBehavior(WS_SCENE_BEHAVIOR_LOG, userMobile,
                    "新增1条用户行为日志", "[" + json + "]");
        } catch (Exception e) {
            logger.error("WebSocket推送行为日志失败: {}", e.getMessage());
        }
    }

    // ============ 线程控制 ============
    /** 可重入锁，防止并发启动/停止操作冲突 */
    private final ReentrantLock lock = new ReentrantLock();
    
    /** 运行状态标志，volatile保证多线程可见性 */
    private volatile boolean running = false;
    
    /** 定时任务线程池 */
    private ScheduledExecutorService scheduler;
    
    /** 停止倒计数门闩，用于优雅停止 */
    private CountDownLatch stopLatch;
    
    // ============ 数据统计 ============
    /** 当前ID，原子操作保证线程安全 */
    private final AtomicLong currentId = new AtomicLong(0);
    
    /** 成功插入计数 */
    private final AtomicLong insertCount = new AtomicLong(0);
    
    /** 失败计数 */
    private final AtomicLong errorCount = new AtomicLong(0);
    
    /** 启动时间戳 */
    private volatile long startTime = 0;
    
    /** 目标手机号 */
    private volatile String targetMobile = "";

    /**
     * 启动数据生成器
     * 
     * @param userMobile 目标手机号
     * @param startId    起始ID（默认从数据库最大ID+1开始）
     * @param intervalMs 插入间隔毫秒（默认1000ms，即1秒1条）
     * @param batchSize  每次插入条数（默认1）
     * @return 执行结果
     */
    @GetMapping("/start")
    public AjaxResult start(
            @RequestParam String userMobile,
            @RequestParam(required = false) Long startId,
            @RequestParam(defaultValue = "1000") Long intervalMs,
            @RequestParam(defaultValue = "1") Integer batchSize)
    {
        // 加锁防止并发启动
        if (!lock.tryLock()) {
            return error("操作进行中，请稍后再试");
        }
        
        try {
            if (running) {
                return error("生成器已在运行中，请先停止");
            }
            
            // 如果未指定起始ID，从数据库查询最大ID
            if (startId == null) {
                Long maxId = testDataInsertMapper.selectMaxId();
                startId = maxId + 1;
            }
            
            // 参数校验
            if (intervalMs < 100) {
                return error("间隔时间不能小于100ms");
            }
            if (batchSize < 1 || batchSize > 100) {
                return error("批量大小必须在1-100之间");
            }
            
            // 初始化状态
            running = true;
            currentId.set(startId);
            insertCount.set(0);
            errorCount.set(0);
            startTime = System.currentTimeMillis();
            targetMobile = userMobile;
            stopLatch = new CountDownLatch(1);
            
            // 创建定时任务线程池（单线程）
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "TestDataGenerator-Thread");
                t.setDaemon(true); // 设置为守护线程
                return t;
            });
            
            // 定时执行插入任务
            final Long finalStartId = startId;
            scheduler.scheduleAtFixedRate(() -> {
                if (!running) return;
                
                try {
                    // 批量插入
                    for (int i = 0; i < batchSize && running; i++) {
                        long id = currentId.getAndIncrement();
                        Date now = new Date();
                        int result = insertRichData(id, userMobile, now);
                        
                        if (result > 0) {
                            insertCount.incrementAndGet();
                            // 【长链接推送】插入成功后实时推送给订阅该手机号的 WebSocket 客户端
                            pushToWebSocket(userMobile, id, now);
                        } else {
                            errorCount.incrementAndGet();
                            logger.warn("插入失败，ID={}, 可能已存在", id);
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    logger.error("插入异常: {}", e.getMessage(), e);
                }
            }, 0, intervalMs, TimeUnit.MILLISECONDS);
            
            return success("生成器启动成功")
                    .put("startId", finalStartId)
                    .put("interval", intervalMs + "ms")
                    .put("batchSize", batchSize)
                    .put("expectedTps", String.format("%.2f 条/秒", batchSize * 1000.0 / intervalMs));
                    
        } finally {
            lock.unlock();
        }
    }

    /**
     * 停止数据生成器（优雅停止）
     * 
     * @param waitMs 等待线程结束的超时时间（默认3秒）
     * @return 执行结果
     */
    @GetMapping("/stop")
    public AjaxResult stop(@RequestParam(defaultValue = "3000") Long waitMs)
    {
        if (!lock.tryLock()) {
            return error("操作进行中，请稍后再试");
        }
        
        try {
            if (!running) {
                return error("生成器未在运行");
            }
            
            // 标记停止
            running = false;
            
            // 关闭线程池
            if (scheduler != null) {
                scheduler.shutdown();
                try {
                    // 等待任务完成
                    if (!scheduler.awaitTermination(waitMs, TimeUnit.MILLISECONDS)) {
                        // 超时强制关闭
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            // 计算运行统计
            long runningTime = System.currentTimeMillis() - startTime;
            double tps = insertCount.get() * 1000.0 / Math.max(runningTime, 1);
            
            return success("生成器已停止")
                    .put("lastId", currentId.get() - 1)
                    .put("totalInserted", insertCount.get())
                    .put("errorCount", errorCount.get())
                    .put("runningTime", runningTime + "ms")
                    .put("avgTps", String.format("%.2f 条/秒", tps));
                    
        } finally {
            lock.unlock();
        }
    }

    /**
     * 查看运行状态
     * 
     * @return 状态信息
     */
    @GetMapping("/status")
    public AjaxResult status()
    {
        if (!running) {
            return success("未运行")
                    .put("running", false)
                    .put("lastInsertCount", insertCount.get())
                    .put("lastErrorCount", errorCount.get());
        }
        
        long runningTime = System.currentTimeMillis() - startTime;
        double tps = insertCount.get() * 1000.0 / Math.max(runningTime, 1);
        
        return success("运行中")
                .put("running", true)
                .put("targetMobile", targetMobile)
                .put("currentId", currentId.get())
                .put("insertCount", insertCount.get())
                .put("errorCount", errorCount.get())
                .put("runningTime", runningTime + "ms")
                .put("avgTps", String.format("%.2f 条/秒", tps));
    }

    /**
     * 单次插入（手动触发）
     * 
     * @param userMobile 用户手机号
     * @param id 数据ID
     * @return 执行结果
     */
    @GetMapping("/insertOnce")
    public AjaxResult insertOnce(
            @RequestParam String userMobile,
            @RequestParam Long id)
    {
        try {
            Date now = new Date();
            int result = insertRichData(id, userMobile, now);

            if (result > 0) {
                return success("插入成功")
                        .put("id", id)
                        .put("time", now);
            } else {
                return error("插入失败，ID可能已存在: " + id);
            }
        } catch (Exception e) {
            logger.error("插入异常", e);
            return error("插入失败: " + e.getMessage());
        }
    }

    /**
     * 应用关闭时优雅停止
     */
    @PreDestroy
    public void onDestroy()
    {
        if (running && scheduler != null) {
            logger.info("应用关闭，停止测试数据生成器");
            running = false;
            scheduler.shutdownNow();
        }
    }
}
