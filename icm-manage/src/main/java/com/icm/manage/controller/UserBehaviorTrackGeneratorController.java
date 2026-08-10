package com.icm.manage.controller;

import com.icm.common.core.controller.BaseController;
import com.icm.common.core.domain.AjaxResult;
import com.icm.common.enums.DataSourceType;
import com.icm.framework.datasource.DynamicDataSourceContextHolder;
import com.icm.manage.mapper.UserBehaviorTrackMapper;
import com.icm.websocket.service.WebSocketService;
import com.alibaba.fastjson2.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 【模拟长连接-数据生成器】用户行为轨迹（ClickHouse）自动插入数据
 *
 * 定位：和 TestDataGeneratorController（往 MySQL 的 tb_real_time_data_verification 插数据，
 * 配合 UserBehaviorLogController#getIncrementalData 模拟长连接）是完全对称的一套东西，
 * 区别只在于这里插的是 ClickHouse 的 tb_easydata_user_trackinfo_complete_local 表，
 * 配合 UserBehaviorTrackController#getIncrementalData 做增量轮询演示。
 *
 * 【核心特性】
 * 1. 固定频率：每秒插入 1 条（ScheduledExecutorService.scheduleAtFixedRate）
 * 2. 内容变长：每条数据的页面标题/链接/事件名按 5 档长度循环，插入长度肉眼可见地变化
 * 3. 自动停止：启动后固定运行 5 分钟，到点自动停止，不需要手动干预
 * 4. 线程安全：ReentrantLock 防并发启停，AtomicLong 无锁计数
 *
 * @author icm
 */
@Api(tags = "用户行为轨迹-数据生成器(模拟长连接演示用)")
@RestController
@RequestMapping("/manage/userBehaviorTrack/generator")
public class UserBehaviorTrackGeneratorController extends BaseController {

    @Autowired
    private UserBehaviorTrackMapper userBehaviorTrackMapper;

    /** 自动运行时长：5 分钟 */
    private static final long AUTO_STOP_MINUTES = 5;

    /**
     * 内容长度按 5 档循环，长度依次递增，方便在前端表格里肉眼看出"每条数据长度不一样"：
     * 页面标题：2 → 5 → 6 → 9 → 12 个字
     */
    private static final String[] TITLES = {
            "首页",
            "商品详情页",
            "购物车结算页",
            "订单确认支付详情页",
            "用户个人中心设置管理页面"
    };
    private static final String[] PAGES = {
            "/home",
            "/product/detail",
            "/cart/checkout",
            "/order/confirm/pay",
            "/user/profile/settings/manage"
    };
    private static final String[] EVENTS = {
            "home_view",
            "product_detail_view",
            "cart_checkout_click",
            "order_confirm_pay_click",
            "user_profile_settings_manage_view"
    };
    private static final String[] EVENT_TYPES = {"pv", "click", "pv", "click", "pv"};

    // ============ 依赖注入 ============
    @Autowired
    private WebSocketService webSocketService;

    /** WebSocket 业务推送场景：用户行为轨迹 */
    private static final String WS_SCENE_BEHAVIOR_TRACK = "behaviorTrack";

    // ============ 线程控制 ============
    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean running = false;
    private ScheduledExecutorService scheduler;

    // ============ 数据统计 ============
    private final AtomicLong seq = new AtomicLong(0);
    private final AtomicLong insertCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    private volatile long startTime = 0;
    private volatile String targetMobile = "";

    /**
     * 启动数据生成器：每秒插入 1 条，运行 5 分钟后自动停止
     *
     * @param phone 目标手机号（需在白名单 sys_white_mobile 里，否则查询接口会拒绝）
     */
    @ApiOperation("启动生成器(每秒1条,自动运行5分钟)")
    @GetMapping("/start")
    public AjaxResult start(@RequestParam String phone) {
        if (!lock.tryLock()) {
            return error("操作进行中，请稍后再试");
        }
        try {
            if (running) {
                return error("生成器已在运行中，请先停止");
            }
            if (phone == null || !phone.matches("^\\d{11}$")) {
                return error("请输入有效的11位手机号");
            }

            running = true;
            targetMobile = phone;
            seq.set(0);
            insertCount.set(0);
            errorCount.set(0);
            startTime = System.currentTimeMillis();

            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "TrackGenerator-Thread");
                t.setDaemon(true);
                return t;
            });

            // 每秒插入一条
            scheduler.scheduleAtFixedRate(() -> {
                if (!running) {
                    return;
                }
                try {
                    insertOne(phone);
                    insertCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    logger.error("[TrackGenerator] 插入ClickHouse失败: {}", e.getMessage(), e);
                }
            }, 0, 1, TimeUnit.SECONDS);

            // 5 分钟后自动停止
            scheduler.schedule(this::stopInternal, AUTO_STOP_MINUTES, TimeUnit.MINUTES);

            return success("生成器启动成功")
                    .put("phone", phone)
                    .put("interval", "1000ms")
                    .put("autoStopAfter", AUTO_STOP_MINUTES + "分钟")
                    .put("startTime", new Date());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 插入一条内容长度递增循环的轨迹数据
     */
    private void insertOne(String phone) {
        int idx = (int) (seq.getAndIncrement() % TITLES.length);
        Date now = new Date();

        try {
            DynamicDataSourceContextHolder.setDataSourceType(DataSourceType.CLICKHOUSE.name());
            userBehaviorTrackMapper.insertTrackData(
                    now,
                    now,
                    phone,
                    EVENT_TYPES[idx],
                    TITLES[idx],
                    PAGES[idx] + "?ts=" + now.getTime(),
                    "模拟长连接演示-第" + (idx + 1) + "档长度",
                    EVENTS[idx]
            );
            // 【长链接推送】插入成功后实时推送给订阅该手机号的 WebSocket 客户端
            pushToWebSocket(phone, now, idx);
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 【长链接推送】将刚插入的一条行为轨迹实时推送给订阅该手机号的 WebSocket 客户端。
     * 无订阅连接时静默跳过，不影响生成器主流程。
     */
    private void pushToWebSocket(String phone, Date now, int idx) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("daytime", now);
            data.put("userId", phone);
            data.put("wtEt", EVENT_TYPES[idx]);
            data.put("wtTi", TITLES[idx]);
            data.put("wtEs", PAGES[idx]);
            data.put("wtEnvName", "模拟长连接演示-第" + (idx + 1) + "档长度");
            data.put("wtEvent", EVENTS[idx]);
            String json = JSON.toJSONString(data);
            webSocketService.pushBehavior(WS_SCENE_BEHAVIOR_TRACK, phone,
                    "新增1条用户行为轨迹", "[" + json + "]");
        } catch (Exception e) {
            logger.error("WebSocket推送行为轨迹失败: {}", e.getMessage());
        }
    }

    /**
     * 停止数据生成器
     */
    @ApiOperation("停止生成器")
    @GetMapping("/stop")
    public AjaxResult stop() {
        if (!lock.tryLock()) {
            return error("操作进行中，请稍后再试");
        }
        try {
            if (!running) {
                return error("生成器未在运行");
            }
            return stopInternal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 实际停止逻辑，供手动 /stop 和 5 分钟自动停止共用
     */
    private AjaxResult stopInternal() {
        if (!running) {
            return success("生成器已停止");
        }
        running = false;

        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        long runningTime = System.currentTimeMillis() - startTime;
        return success("生成器已停止")
                .put("totalInserted", insertCount.get())
                .put("errorCount", errorCount.get())
                .put("runningTime", runningTime + "ms");
    }

    /**
     * 查看运行状态
     */
    @ApiOperation("查看运行状态")
    @GetMapping("/status")
    public AjaxResult status() {
        if (!running) {
            return success("未运行")
                    .put("running", false)
                    .put("lastInsertCount", insertCount.get())
                    .put("lastErrorCount", errorCount.get());
        }
        long runningTime = System.currentTimeMillis() - startTime;
        long remainSeconds = Math.max(0, AUTO_STOP_MINUTES * 60 - runningTime / 1000);
        return success("运行中")
                .put("running", true)
                .put("targetMobile", targetMobile)
                .put("insertCount", insertCount.get())
                .put("errorCount", errorCount.get())
                .put("runningTime", runningTime + "ms")
                .put("remainSeconds", remainSeconds);
    }

    /**
     * 应用关闭时优雅停止
     */
    @PreDestroy
    public void onDestroy() {
        if (running && scheduler != null) {
            logger.info("应用关闭，停止用户行为轨迹数据生成器");
            running = false;
            scheduler.shutdownNow();
        }
    }
}
