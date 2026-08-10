package com.icm.manage.controller;

import com.icm.common.annotation.Log;
import com.icm.common.core.controller.BaseController;
import com.icm.common.core.page.TableDataInfo;
import com.icm.common.enums.BusinessType;
import com.icm.common.exception.ServiceException;
import com.icm.common.utils.poi.ExcelUtil;
import com.icm.manage.domain.dto.UserBehaviorTrackQueryDTO;
import com.icm.manage.domain.vo.TbUserBehaviorTrackVo;
import com.icm.manage.service.IUserBehaviorTrackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户行为轨迹查询 Controller
 */
@Api(tags = "用户行为轨迹查询")
@RestController
@RequestMapping("/manage/userBehaviorTrack")
public class UserBehaviorTrackController extends BaseController {

    @Autowired
    private IUserBehaviorTrackService userBehaviorTrackService;

    /**
     * 查询用户行为轨迹列表
     */
    @ApiOperation("查询用户行为轨迹列表")
    @GetMapping("/list")
    public TableDataInfo list(UserBehaviorTrackQueryDTO query) {
        List<TbUserBehaviorTrackVo> list = userBehaviorTrackService.selectUserBehaviorTrackList(query);
        Long total = userBehaviorTrackService.countUserBehaviorTrack(query);

        // 手动组装分页结果
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(com.icm.common.constant.HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        // 防止 total 为 null 时自动拆箱引发 NullPointerException
        rspData.setTotal(total != null ? total : 0L);

        return rspData;
    }

    /**
     * 导出用户行为轨迹列表(Excel)
     */
    @ApiOperation("导出用户行为轨迹列表")
    @Log(title = "用户行为轨迹查询", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserBehaviorTrackQueryDTO query) {
        List<TbUserBehaviorTrackVo> list = userBehaviorTrackService.selectUserBehaviorTrackAll(query);

        // 需求：未查询(或无数据)不允许导出
        if (list == null || list.isEmpty()) {
            throw new ServiceException("未查询到当前手机号的用户行为轨迹数据");
        }

        ExcelUtil<TbUserBehaviorTrackVo> util = new ExcelUtil<>(TbUserBehaviorTrackVo.class);
        util.exportExcel(response, list, "用户行为轨迹数据");
    }

    /**
     * 【问题复现用】用 RuoYi 通用 PageHelper 分页查询 ClickHouse，复现双 ORDER BY 语法错误。
     * 示例：GET /manage/userBehaviorTrack/demoPageHelperBug?phone=13800138000
     *      &beginTime=2026-06-28 00:00:00&endTime=2026-07-02 23:59:59
     *      &orderByColumn=daytime&isAsc=asc
     * 不带 orderByColumn 时正常返回；带上后应复现 ClickHouse 语法错误。
     */
    @ApiOperation("【问题复现】PageHelper双ORDER BY")
    @GetMapping("/demoPageHelperBug")
    public com.icm.common.core.domain.AjaxResult demoPageHelperBug(UserBehaviorTrackQueryDTO query) {
        List<TbUserBehaviorTrackVo> list = userBehaviorTrackService.demoPageHelperOrderByBug(query);
        return success(list);
    }

    /**
     * 【模拟长连接】查询增量数据，供前端轮询获取实时新增的轨迹数据。
     * 与 UserBehaviorLogController#getIncrementalData 是同一套设计（增量轮询代替 WebSocket），
     * 只是数据源换成了 ClickHouse。配合 UserBehaviorTrackGeneratorController 的自动生成器一起演示：
     * 生成器每秒往 ClickHouse 插一条数据，前端每 2 秒调这个接口拉一次增量。
     *
     * 面试踩坑点1：queryTime 用 Long（epoch 毫秒）而不是格式化字符串做前后端交互协议。
     * 最早用过 {@code @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") Date queryTime}
     * 让 Spring 自动绑定，实测在这套环境下会静默失败——不抛异常，直接绑成 null
     * （因为 required=false），导致"增量过滤"形同虚设，每次都把全量历史数据查出来，
     * 且没有任何报错提示，非常隐蔽。UserBehaviorLogController#getIncrementalData 里
     * 那行被注释掉的 SimpleDateFormat 解析代码就是同一个坑的历史遗留（注释写着
     * "公司环境不兼容，传入String转Date兼容时间格式"）。
     *
     * 面试踩坑点2：改成手动 SimpleDateFormat 解析字符串后，又踩了第二个坑——
     * ClickHouse JDBC 驱动读回的 daytime 跟写入时的瞬时值之间有 8 小时的系统性偏差，
     * 如果把服务端返回的 queryEndTime（格式化字符串）原样传回来给 SimpleDateFormat
     * 用本地时区重新 parse，会被二次误解释，导致下一轮轮询把已经推送过的数据又推一遍。
     * 最终方案：queryTime/queryEndTime 全程用 epoch 毫秒数值传递，不经过任何
     * 字符串格式化/解析，彻底规避时区解析问题。
     *
     * 示例：GET /manage/userBehaviorTrack/incremental?phone=13800138000
     *      首次调用不传 queryTime；此后每次把上一次返回的 queryEndTime（毫秒数）原样传回
     */
    @ApiOperation("【模拟长连接】增量轮询查询")
    @GetMapping("/incremental")
    public com.icm.common.core.domain.AjaxResult getIncrementalData(
            @RequestParam String phone,
            @RequestParam(required = false) Long queryTime) {
        if (phone == null || phone.trim().isEmpty()) {
            return error("请输入有效的11位手机号");
        }
        Date parsedQueryTime = queryTime != null ? new Date(queryTime) : null;
        try {
            Map<String, Object> result = userBehaviorTrackService.selectTrackIncrementalData(phone, parsedQueryTime);
            return success(result);
        } catch (Exception e) {
            logger.error("查询用户行为轨迹增量数据失败", e);
            return error(e.getMessage() != null ? e.getMessage() : "查询失败，请稍后重试");
        }
    }
}


