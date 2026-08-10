package com.dkd.manage.domain;


import com.dkd.common.annotation.Excel;
import com.dkd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

/**
 * 插码管理平台-字段维护对象 tb_coding_scheme_field_maintenance
 */
@Data
@EqualsAndHashCode(callSuper = true)

public class TbCodingSchemeFieldMaintenanceDetailVo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Excel(name = "主键ID")
    private String id;

    /** 字段名称 */
    @Excel(name = "字段名称")
    private String fieldName;

    /** 字段标识 */
    @Excel(name = "字段标识")
    private String fieldIdentification;

    // 原有的字段
    @Excel(name = "维护时间")
    private String maintenanceTime;

    @Excel(name = "适用范围")
    private String applyScope;


    // --- 适用范围 (3个 0/1 字段) ---
    /** 是否适用H5 (1:是 0:否) */
    @Excel(name = "适用H5", readConverterExp = "0=否,1=是")
    private Boolean isScopeH5;

    /** 是否适用原生 (1:是 0/否) */
    @Excel(name = "适用原生", readConverterExp = "0=否,1=是")
    private Boolean isScopeNative;

    /** 是否适用小程序 (1:是 0/否) */
    @Excel(name = "适用小程序", readConverterExp = "0=否,1=是")
    private Boolean isScopeMini;

    // --- 其他核心字段 ---

    /** 是否必传 (1:是 0/否) */
    @Excel(name = "是否必传", readConverterExp = "0=否,1=是")
    private Boolean isRequired;

    /** 最大长度 */
    @Excel(name = "最大长度")
    private Integer maxLength;


    /** 是否包含数字 (1:是 0/否) */
    @Excel(name = "包含数字", readConverterExp = "0=否,1=是")
    private Boolean isContentDigit;

    /** 是否包含字母 (1:是 0/否) */
    @Excel(name = "包含字母", readConverterExp = "0=否,1=是")
    private Boolean isContentLetter;

    /** 是否包含汉字 (1:是 0/否) */
    @Excel(name = "包含汉字", readConverterExp = "0=否,1:是")
    private Boolean isContentChinese;

    /** 是否包含下划线 (1:是 0/否) */
    @Excel(name = "包含下划线", readConverterExp = "0=否,1=是")
    private Boolean isContentUnderscore;

    /** 业务口径说明 */
    @Excel(name = "业务口径")
    @Size(max = 10, message = "业务口径长度不能超过500个字符")
    private String businessDescription;

    /** 技术口径说明 */
    @Excel(name = "技术口径")
    @Size(max = 10, message = "业务口径长度不能超过500个字符")
    private String technicalDescription;
}