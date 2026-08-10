package com.icm.manage.controller;

import com.icm.common.annotation.Log;
import com.icm.common.core.controller.BaseController;
import com.icm.common.core.domain.AjaxResult;
import com.icm.common.core.page.TableDataInfo;
import com.icm.common.enums.BusinessType;
import com.icm.manage.domain.DataLevelLabel;
import com.icm.manage.service.IDataLevelLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据等级标注管理 Controller
 * 
 * @author ruoyi
 * @date 2025-12-16
 */
@RestController
@RequestMapping("/data_level")
public class TbDataLevelLabelController extends BaseController {

    @Autowired
    private IDataLevelLabelService dataLevelLabelService;

    /**
     * 查询数据等级标注列表
     * 
     * @param dataLevelLabel 数据等级标注对象
     * @return 数据等级标注集合
     */
    @GetMapping("/list")
    public TableDataInfo list(DataLevelLabel dataLevelLabel)
    {
        startPage();
        List<DataLevelLabel> list = dataLevelLabelService.selectDataLevelLabelList(dataLevelLabel);
        return getDataTable(list);
    }

    /**
     * 新增数据等级标注
     * 
     * @param dataLevelLabel 数据等级标注对象
     * @return 操作结果
     */
    @Log(title = "保存等级标注", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataLevelLabel dataLevelLabel)
    {
        // 1. 【批量查重】去数据库看看，这一批里有没有已经存在的
        List<DataLevelLabel> existingList = dataLevelLabelService.checkBatchUnique(dataLevelLabel);

        // 2. 【重复处理】只要主键(channel_id,event_code)已存在，就返回列表+提示，让前端决定是否覆盖
        if (existingList != null && !existingList.isEmpty() && !dataLevelLabel.isForceUpdate()) {
            // 拼接 msg：每条一行，带中文说明
            String duplicateText = existingList.stream()
                    .map(item -> "渠道号：" + (item.getChannelId() == null ? "" : item.getChannelId())
                            + " 事件类型：" + (item.getEventCode() == null ? "" : item.getEventCode())
                            + " 数据等级：" + formatDataLevel(item.getDataLevel()))
                    .collect(Collectors.joining("\n"));
            String msg = "当前有重复数据，请确认是否覆盖\n" + duplicateText;

            AjaxResult result = AjaxResult.success(msg);
            result.put("needConfirm", true);
            // 返回实体类列表（含 channelId/eventCode/dataLevel=是否重要）
            result.put("existingList", existingList);
            return result;
        }
        return toAjax(dataLevelLabelService.insertDataLevelLabel(dataLevelLabel));
    }

    /**
     * 修改数据等级标注
     * 
     * @param dataLevelLabel 数据等级标注对象
     * @return 操作结果
     */
    @Log(title = "数据等级标注", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataLevelLabel dataLevelLabel)
    {
        return toAjax(dataLevelLabelService.updateDataLevelLabel(dataLevelLabel));
    }

    /**
     * 删除数据等级标注
     * 
     * @param id 数据等级标注主键
     * @return 操作结果
     */
    @Log(title = "数据等级标注", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(dataLevelLabelService.deleteDataLevelLabelById(id));
    }

    /**
     * 获取事件类型下拉列表
     */
    @GetMapping("/options")
    public AjaxResult getOptions()
    {
        DataLevelLabel dataLevelLabel = new DataLevelLabel();

        // 1. 查出不重复的事件 Code 列表
        List<String> eventCodes = dataLevelLabelService.selectAllEventCodes();
        // 2. 存入实体类的 eventCodes 字段
        dataLevelLabel.setEventCodes(eventCodes);

        // 3. 查出不重复的渠道 ID 列表
        List<String> channelIds = dataLevelLabelService.selectAllChannelIds();
        // 4. 存入实体类的 channelIds 字段
        dataLevelLabel.setChannelIds(channelIds);

        return success(dataLevelLabel);
    }

    /**
     * 数据等级显示转换：1=核心，2=重要（兼容 CORE/IMPORTANT）
     */
    private String formatDataLevel(String dataLevel) {
        if (dataLevel == null) {
            return "";
        }
        if ("1".equals(dataLevel)) {
            return "核心";
        }
        if ("2".equals(dataLevel)) {
            return "重要";
        }
        return dataLevel;
    }
}