package com.icm.manage.mapper;

import com.icm.manage.domain.TbCodingSchemeFieldMaintenanceDetailVo;

import java.util.List;

public interface MetaDataManageMapper {
    
    /**
     * 查询字段维护列表
     *
     * @param tbCodingSchemeFieldMaintenance 字段维护对象
     * @return 字段维护列表
     */
    List<TbCodingSchemeFieldMaintenanceDetailVo> selectMetaDataList(TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance);
    
    /**
     * 根据ID查询字段维护信息
     *
     * @param id 主键ID
     * @return 字段维护信息
     */
    TbCodingSchemeFieldMaintenanceDetailVo selectMetaDataById(String id);
    
    /**
     * 新增字段维护
     *
     * @param tbCodingSchemeFieldMaintenance 字段维护对象
     * @return 结果
     */
    int insertMetaData(TbCodingSchemeFieldMaintenanceDetailVo tbCodingSchemeFieldMaintenance);

}