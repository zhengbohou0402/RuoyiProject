package com.dkd.manage.service;

import com.dkd.manage.domain.TbCodingSchemeFieldMaintenanceDetailVo;

import java.util.List;

public interface IMetaDataManageService {

    List<TbCodingSchemeFieldMaintenanceDetailVo> selectMetaDataList(TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance);
    
    int insertMetaData(TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance);

    TbCodingSchemeFieldMaintenanceDetailVo selectMetaDataById(String id);
}