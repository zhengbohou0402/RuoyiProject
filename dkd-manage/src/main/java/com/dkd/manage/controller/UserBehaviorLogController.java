package com.dkd.manage.controller;

import com.dkd.common.annotation.Log;
import com.dkd.common.core.controller.BaseController;
import com.dkd.common.core.domain.AjaxResult;
import com.dkd.common.core.page.TableDataInfo;
import com.dkd.common.enums.BusinessType;
import com.dkd.common.utils.poi.ExcelUtil;
import com.dkd.manage.domain.UserBehaviorLog;
import com.dkd.manage.service.IUserBehaviorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户行为日志Controller
 * 
 * @author dkd
 * @date 2026-01-27
 */
@RestController
@RequestMapping("/manage/behaviorLog")
public class UserBehaviorLogController extends BaseController
{
    @Autowired
    private IUserBehaviorLogService userBehaviorLogService;

    /**
     * 查询用户行为日志列表
     */
    @GetMapping("/list")
    public TableDataInfo list(UserBehaviorLog userBehaviorLog)
    {
        startPage();
        List<UserBehaviorLog> list = userBehaviorLogService.selectUserBehaviorLogList(userBehaviorLog);
        return getDataTable(list);
    }

    /**
     * 导出用户行为日志列表
     */
    @Log(title = "用户行为日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserBehaviorLog userBehaviorLog)
    {
        List<UserBehaviorLog> list = userBehaviorLogService.selectUserBehaviorLogList(userBehaviorLog);
        ExcelUtil<UserBehaviorLog> util = new ExcelUtil<UserBehaviorLog>(UserBehaviorLog.class);
        util.exportExcel(response, list, "用户行为日志数据");
    }

    /**
     * 获取用户行为日志详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(userBehaviorLogService.selectUserBehaviorLogById(id));
    }

    /**
     * 查询增量数据(用于模拟长链接)
     * 此接口用于前端轮询获取实时数据
     *
     * 面试踩坑点：queryTime 原来用 {@code @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") Date}
     * 让 Spring 自动做字符串转 Date，实测在这套环境下这个转换会直接失败并返回 400
     * （"Required request parameter 'queryTime' for method parameter type Date is present
     * but converted to null"），这个接口在这次修复之前处于实际不可用状态。
     * 下面那两行被注释掉的 SimpleDateFormat 手动解析代码，就是同一个坑的历史遗留
     * （原注释："公司环境不兼容，传入String转Date兼容时间格式"）。
     * 现在改成 Long（epoch 毫秒）做前后端交互协议，不经过任何字符串格式化/解析，
     * 从根上避免时区解析问题，跟 UserBehaviorTrackController#getIncrementalData 保持一致。
     */
    @GetMapping("/incremental")
    public AjaxResult getIncrementalData(
            @RequestParam String userMobile,
            @RequestParam(required = false) Long queryTime)
    {
        // 校验手机号格式
        if (userMobile == null || userMobile.trim().isEmpty()) {
            return error("请输入有效的11位手机号");
        }

        // 验证手机号格式:11位数字
        Pattern pattern = Pattern.compile("^\\d{11}$");
        if (!pattern.matcher(userMobile).matches()) {
            return error("请输入有效的11位手机号");
        }

        Date parsedQueryTime = queryTime != null ? new Date(queryTime) : null;

        try {
            Map<String, Object> result = userBehaviorLogService.selectIncrementalData(userMobile, parsedQueryTime);
            return success(result);
        } catch (Exception e) {
            logger.error("查询用户行为日志失败", e);
            return error("链接启动失败,请稍后重试");
        }
    }

    /**
     * 新增用户行为日志
     */
    @Log(title = "用户行为日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserBehaviorLog userBehaviorLog)
    {
        return toAjax(userBehaviorLogService.insertUserBehaviorLog(userBehaviorLog));
    }

    /**
     * 修改用户行为日志
     */
    @Log(title = "用户行为日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserBehaviorLog userBehaviorLog)
    {
        return toAjax(userBehaviorLogService.updateUserBehaviorLog(userBehaviorLog));
    }

    /**
     * 删除用户行为日志
     */
    @Log(title = "用户行为日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(userBehaviorLogService.deleteUserBehaviorLogByIds(ids));
    }
}
