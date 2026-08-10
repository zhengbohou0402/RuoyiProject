package com.icm.manage.controller;

import com.icm.common.annotation.Log;
import com.icm.common.core.controller.BaseController;
import com.icm.common.core.domain.AjaxResult;
import com.icm.common.core.page.TableDataInfo;
import com.icm.common.enums.BusinessType;
import com.icm.manage.domain.TbCodingSchemeFieldMaintenanceDetailVo;
import com.icm.manage.service.IMetaDataManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metaData")
public class MetaDataManageController extends BaseController {

    @Autowired
    private IMetaDataManageService metaDataManageService;

    @GetMapping("/listAll")
    public TableDataInfo listAll(TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance){
        startPage();
        List<TbCodingSchemeFieldMaintenanceDetailVo> list = metaDataManageService.selectMetaDataList(tbCodingSchemeFieldMaintenance);
        return getDataTable((List<?>)list);
    }

    @PostMapping
    @Log(title = "插码管理平台-元数据管理-新增属性", businessType = BusinessType.INSERT)
    public AjaxResult add(@Validated @RequestBody TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance){
        return toAjax(metaDataManageService.insertMetaData(tbCodingSchemeFieldMaintenance));
    }

    @GetMapping("/attribute_info")
    public AjaxResult getDetail(@RequestParam("id") String id){
        TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance = metaDataManageService.selectMetaDataById(id);
        return success(tbCodingSchemeFieldMaintenance);
    }
}