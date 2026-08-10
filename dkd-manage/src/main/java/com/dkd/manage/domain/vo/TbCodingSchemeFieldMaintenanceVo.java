package com.dkd.manage.domain.vo;

import com.dkd.common.annotation.Excel;
import com.dkd.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class TbCodingSchemeFieldMaintenanceVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Excel(name = "主键ID")
    private Long id;

    /** 字段名称 */
    @Excel(name = "字段名称")
    private String fieldName;

    /** 字段标识 */
    @Excel(name = "字段标识")
    private String fieldIdentification;


    @Excel(name = "适用范围")
    private String applyScope;

    @Excel(name = "是否必填")
    private Boolean isRequired;
}
