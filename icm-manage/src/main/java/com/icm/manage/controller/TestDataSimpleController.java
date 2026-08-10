package com.icm.manage.controller;

import com.icm.common.core.controller.BaseController;
import com.icm.common.core.domain.AjaxResult;
import com.icm.manage.mapper.TestDataInsertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试数据生成器Controller（简单版）
 * 
 * 【简单实现】
 * 1. 基础功能 - 启动/停止/状态查询
 * 2. 单线程 - 一个后台线程循环插入
 * 3. 简单控制 - volatile标志位控制启停
 * 4. 基础统计 - 记录插入数量和错误数
 * 
 * 【适用场景】
 * - 快速原型开发
 * - 简单的测试数据生成
 * - 学习并发编程基础
 * 
 * 【缺点】
 * - 没有线程池管理
 * - 没有优雅关闭机制
 * - 并发控制较弱
 * - 统计功能简单
 * 
 * @author icm
 * @date 2026-01-29
 */
@RestController
@RequestMapping("/manage/testDataSimple")
public class TestDataSimpleController extends BaseController
{
    @Autowired
    private TestDataInsertMapper testDataInsertMapper;

    /**
     * 内容按 5 档循环，长度依次递增，方便肉眼看出每条数据的差异
     * （跟 TestDataGeneratorController 专业版用的是同一套档位数据，保持两边对比一致）
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

    /** 运行标志 */
    private volatile boolean running = false;
    
    /** 当前ID */
    private AtomicLong currentId = new AtomicLong(0);
    
    /** 插入计数 */
    private AtomicLong insertCount = new AtomicLong(0);
    
    /** 目标手机号 */
    private String userMobile = "";

    /**
     * 启动数据生成（简单版）
     * 
     * @param mobile 手机号
     * @param startId 起始ID（可选，默认从最大ID+1开始）
     * @return 结果
     */
    @GetMapping("/start")
    public AjaxResult start(
            @RequestParam String mobile,
            @RequestParam(required = false) Long startId)
    {
        if (running) {
            return error("已经在运行中");
        }
        
        // 如果未指定起始ID，从数据库查询
        if (startId == null) {
            Long maxId = testDataInsertMapper.selectMaxId();
            startId = maxId + 1;
        }
        
        running = true;
        userMobile = mobile;
        currentId.set(startId);
        insertCount.set(0);
        
        // 开启新线程执行插入
        new Thread(() -> {
            while (running) {
                try {
                    long id = currentId.getAndIncrement();
                    Date now = new Date();

                    int result = insertRichData(id, userMobile, now);
                    
                    if (result > 0) {
                        insertCount.incrementAndGet();
                    }
                    
                    // 每秒插入一条
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    logger.error("插入失败", e);
                }
            }
        }, "SimpleTestDataThread").start();
        
        return success("启动成功，从ID=" + startId + " 开始");
    }

    /**
     * 停止数据生成
     * 
     * @return 结果
     */
    @GetMapping("/stop")
    public AjaxResult stop()
    {
        if (!running) {
            return error("未在运行");
        }
        
        running = false;
        
        return success("已停止")
                .put("currentId", currentId.get())
                .put("insertCount", insertCount.get());
    }

    /**
     * 查看状态
     * 
     * @return 状态信息
     */
    @GetMapping("/status")
    public AjaxResult status()
    {
        return success(running ? "运行中" : "已停止")
                .put("running", running)
                .put("mobile", userMobile)
                .put("currentId", currentId.get())
                .put("insertCount", insertCount.get());
    }
    
    /**
     * 单次插入
     * 
     * @param mobile 手机号
     * @param id ID
     * @return 结果
     */
    @GetMapping("/insertOnce")
    public AjaxResult insertOnce(
            @RequestParam String mobile,
            @RequestParam Long id)
    {
        try {
            Date now = new Date();
            int result = insertRichData(id, mobile, now);

            if (result > 0) {
                return success("插入成功").put("id", id);
            } else {
                return error("插入失败，ID可能已存在");
            }
        } catch (Exception e) {
            return error("插入失败: " + e.getMessage());
        }
    }
}
