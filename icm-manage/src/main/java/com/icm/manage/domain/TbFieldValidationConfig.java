package com.icm.manage.domain;

import com.icm.common.core.domain.BaseEntity;
import com.icm.common.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 字段校验规则配置对象 tb_field_validation_config
 *
 * @author wyq
 * @date 2025-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TbFieldValidationConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** 数据源标识（支持多个，逗号分隔） */
    @Excel(name = "数据源标识")
    @NotBlank(message = "数据源标识不能为空")
    @Size(max = 500, message = "数据源标识长度不能超过500个字符")
    private String datasourceId;

    /** ClickHouse表名 */
    @Excel(name = "表名")
    @Size(max = 100, message = "表名长度不能超过100个字符")
    private String tableName;

    /** 抽样条数 */
    @Excel(name = "抽样条数")
    @NotNull(message = "抽样条数不能为空")
    private Integer sampleCount;

    /** 字段标识 */
    @Excel(name = "字段标识")
    @NotBlank(message = "字段标识不能为空")
    @Size(max = 100, message = "字段标识长度不能超过100个字符")
    private String fieldIdentification;

    /** 字段中文名 */
    @Excel(name = "字段名称")
    @Size(max = 100, message = "字段名称长度不能超过100个字符")
    private String fieldName;

    /** 非空校验 0-否 1-是 */
    @Excel(name = "非空校验", readConverterExp = "0=否,1=是")
    @NotNull(message = "非空校验不能为空")
    private Integer isNotNull;

    /** 是否加密字段 0-否 1-是 */
    @Excel(name = "是否加密", readConverterExp = "0=否,1=是")
    private Integer isEncrypted;

    /** 格式校验类型 date-日期, url-URL, none-无 */
    @Excel(name = "格式校验类型")
    @Size(max = 20, message = "格式校验类型长度不能超过20个字符")
    private String formatValidationType;

    /** 日期格式 */
    @Excel(name = "日期格式")
    @Size(max = 50, message = "日期格式长度不能超过50个字符")
    private String dateFormat;

    /** URL协议 http/https/both */
    @Excel(name = "URL协议")
    @Size(max = 20, message = "URL协议长度不能超过20个字符")
    private String urlProtocol;

    /** 包含数字 0-否 1-是 */
    @Excel(name = "包含数字", readConverterExp = "0=否,1=是")
    private Integer isContentNumber;

    /** 包含汉字 0-否 1-是 */
    @Excel(name = "包含汉字", readConverterExp = "0=否,1=是")
    private Integer isContentChinese;

    /** 包含小写字母 0-否 1-是 */
    @Excel(name = "包含小写字母", readConverterExp = "0=否,1=是")
    private Integer isContentLowercase;

    /** 包含大写字母 0-否 1-是 */
    @Excel(name = "包含大写字母", readConverterExp = "0=否,1=是")
    private Integer isContentUppercase;

    /** 自定义正则表达式 */
    @Excel(name = "自定义正则")
    @Size(max = 500, message = "自定义正则长度不能超过500个字符")
    private String customRegex;

    /** 最小长度 */
    @Excel(name = "最小长度")
    private Integer lengthMin;

    /** 最大长度 */
    @Excel(name = "最大长度")
    private Integer lengthMax;

    /** 枚举值（逗号分隔） */
    @Excel(name = "枚举值")
    private String enumValues;

    /** 是否启用 0-否 1-是 */
    @Excel(name = "是否启用", readConverterExp = "0=否,1=是")
    private Integer isEnabled;
}
