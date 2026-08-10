package com.icm.manage.service.impl;

import com.icm.manage.domain.TbCodingSchemeFieldMaintenanceDetailVo;
import com.icm.manage.mapper.MetaDataManageMapper;
import com.icm.manage.service.IMetaDataManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetaDataManageServiceImpl implements IMetaDataManageService {

    @Autowired
    private MetaDataManageMapper metaDataManageMapper;

    @Override
    public List<TbCodingSchemeFieldMaintenanceDetailVo> selectMetaDataList(TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance) {
        return metaDataManageMapper.selectMetaDataList(tbCodingSchemeFieldMaintenance);
    }

    @Override
    public int insertMetaData(TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance) {
        // 1. 【新增逻辑】根据 3 个开关算出 applyScope 字符串
        List<String> scopeList = new ArrayList<>();

        // 判断 H5
        if (Integer.valueOf(1).equals(tbCodingSchemeFieldMaintenance.getIsScopeH5())) {
            scopeList.add("H5");
        }
        // 判断小程序
        if (Integer.valueOf(1).equals(tbCodingSchemeFieldMaintenance.getIsScopeMini())) {
            scopeList.add("小程序");
        }
        // 判断原生
        if (Integer.valueOf(1).equals(tbCodingSchemeFieldMaintenance.getIsScopeNative())) {
            scopeList.add("原生");
        }

        // 拼接成 "H5/小程序" 这种格式
        String finalScopeStr = String.join("/", scopeList);

        // 2. 把算好的值存入对象
        tbCodingSchemeFieldMaintenance.setApplyScope(finalScopeStr);

        return metaDataManageMapper.insertMetaData(tbCodingSchemeFieldMaintenance);
    }

    @Override
    public TbCodingSchemeFieldMaintenanceDetailVo selectMetaDataById(String id) {
        return metaDataManageMapper.selectMetaDataById(id);
    }
}