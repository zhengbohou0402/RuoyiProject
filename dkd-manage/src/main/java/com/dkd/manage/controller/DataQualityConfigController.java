package com.dkd.manage.controller;

import com.dkd.common.annotation.Log;
import com.dkd.common.core.controller.BaseController;
import com.dkd.common.core.domain.AjaxResult;
import com.dkd.common.core.page.TableDataInfo;
import com.dkd.common.enums.BusinessType;
import com.dkd.manage.domain.TbFieldValidationConfig;
import com.dkd.manage.domain.dto.ConfigSaveDTO;
import com.dkd.manage.domain.vo.ChannelOptionVO;
import com.dkd.manage.domain.vo.FieldConfigVO;
import com.dkd.manage.service.ITbCodingSchemeChannelNumberManageService;
import com.dkd.manage.service.ITbFieldValidationConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据质量规则配置Controller
 *
 * @author wyq
 * @date 2025-12-22
 */
@Api(tags = "数据质量-规则配置管理")
@RestController
@RequestMapping("/quality/config")
public class DataQualityConfigController extends BaseController {

    @Autowired
    private ITbFieldValidationConfigService configService;

    @Autowired
    private ITbCodingSchemeChannelNumberManageService channelManageService;

    /**
     * 查询数据源（渠道）下拉选项
     */
    @ApiOperation("查询数据源下拉选项")
    @GetMapping("/datasourceOptions")
    public AjaxResult getDatasourceOptions() {
        List<ChannelOptionVO> list = channelManageService.selectChannelOptions();
        return success(list);
    }

    /**
     * 查询字段列表（含所有规则配置）
     * 返回全部字段，有规则的显示规则信息，无规则的显示空
     */
    @ApiOperation("查询字段列表（含所有规则配置）")
    @GetMapping("/fields")
    public AjaxResult getFieldList() {
        List<FieldConfigVO> list = configService.selectFieldListWithConfig(null);
        return success(list);
    }

    /**
     * 保存规则配置(批量)
     */
    @ApiOperation("保存规则配置(批量)")
    @PostMapping
    @Log(title = "数据质量-保存规则配置", businessType = BusinessType.INSERT)
    public AjaxResult saveRules(@Validated @RequestBody ConfigSaveDTO configSaveDTO) {
        Map<String, Object> result = configService.saveConfigs(configSaveDTO);
        return success(result);
    }

    /**
     * 查询规则配置列表
     */
    @ApiOperation("查询规则配置列表")
    @GetMapping("/rules")
    public TableDataInfo getRuleList(TbFieldValidationConfig config) {
        startPage();
        List<TbFieldValidationConfig> list = configService.selectTbFieldValidationConfigList(config);
        return getDataTable(list);
    }

    /**
     * 查询规则配置详情
     */
    @ApiOperation("查询规则配置详情")
    @GetMapping("/rules/{id}")
    public AjaxResult getRuleDetail(@PathVariable("id") Long id) {
        TbFieldValidationConfig config = configService.selectTbFieldValidationConfigById(id);
        return success(config);
    }

    /**
     * 修改规则配置
     */
    @ApiOperation("修改规则配置")
    @PutMapping
    @Log(title = "数据质量-修改规则配置", businessType = BusinessType.UPDATE)
    public AjaxResult updateRule(@Validated @RequestBody TbFieldValidationConfig config) {
        return toAjax(configService.updateTbFieldValidationConfig(config));
    }

    /**
     * 删除规则配置
     */
    @ApiOperation("删除规则配置")
    @DeleteMapping("/rules/{ids}")
    @Log(title = "数据质量-删除规则配置", businessType = BusinessType.DELETE)
    public AjaxResult removeRules(@PathVariable Long[] ids) {
        return toAjax(configService.deleteTbFieldValidationConfigByIds(ids));
    }
}
