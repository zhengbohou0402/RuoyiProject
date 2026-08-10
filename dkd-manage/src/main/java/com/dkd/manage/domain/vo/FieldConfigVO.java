package com.dkd.manage.domain.vo;


import com.dkd.common.annotation.Excel;
import com.dkd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段+规则配置VO
 * 用于字段列表查询接口响应
 *
 * @author wyq
 * @date 2025-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldConfigVO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 配置ID（如果已配置） */
    @Excel(name = "配置ID")
    private Long configId;

    /** 字段标识 */
    @Excel(name = "字段标识")
    private String fieldIdentification;

    /** 字段中文名 */
    @Excel(name = "字段名称")
    private String fieldName;

    /** 字段数据类型 */
    @Excel(name = "数据类型")
    private String fieldType;

    /** 维护时间 */
    @Excel(name = "维护时间")
    private String maintenanceTime;

    /** 数据源标识 */
    private String datasourceId;

    /** 表名 */
    private String tableName;

    /** 抽样条数 */
    @Excel(name = "抽样条数")
    private Integer sampleCount;

    /** 非空校验 0-否 1-是 */
    @Excel(name = "非空校验", readConverterExp = "0=否,1=是")
    private Integer isNotNull;

    /** 格式校验类型 */
    @Excel(name = "格式校验类型")
    private String formatValidationType;

    /** 日期格式 */
    private String dateFormat;

    /** URL协议 */
    private String urlProtocol;

    /** 包含数字 0-否 1-是 */
    @Excel(name = "包含数字", readConverterExp = "0=否,1=是")
    private Integer isContentNumber;

    /** 包含汉字 0-否 1-是 */
    @Excel(name = "包含汉字", readConverterExp = "0=否,1=是")
    private Integer isContentChinese;

    /** 包含小写字母 0-否 1-是 */
    @Excel(name = "包含小写", readConverterExp = "0=否,1=是")
    private Integer isContentLowercase;

    /** 包含大写字母 0-否 1-是 */
    @Excel(name = "包含大写", readConverterExp = "0=否,1=是")
    private Integer isContentUppercase;

    /** 自定义正则表达式 */
    private String customRegex;

    /** 最小长度 */
    @Excel(name = "最小长度")
    private Integer lengthMin;

    /** 最大长度 */
    @Excel(name = "最大长度")
    private Integer lengthMax;

    /** 枚举值（逗号分隔） */
    private String enumValues;

    /** 是否启用 0-否 1-是 */
    @Excel(name = "是否启用", readConverterExp = "0=否,1=是")
    private Integer isEnabled;
}
