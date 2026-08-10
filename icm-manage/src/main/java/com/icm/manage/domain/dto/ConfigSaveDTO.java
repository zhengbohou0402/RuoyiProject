package com.icm.manage.domain.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 保存规则配置DTO
 * 用于保存规则配置接口请求
 *
 * @author wyq
 * @date 2025-12-22
 */
@Data
public class ConfigSaveDTO {
    /**
     * 数据源标识列表(支持多选)
     */
    @NotEmpty(message = "datasource_ids不能为空")
    private List<String> datasourceIds;

    /**
     * 表名(可选，默认为tb_easydata_user_trackinfo_complete_all)
     */
    private String tableName;

    /**
     * 抽样条数
     */
    @NotNull(message = "抽样条数不能为空")
    private Integer sampleCount;

    /**
     * 字段配置列表
     */
    @Valid
    @NotEmpty(message = "字段配置列表不能为空")
    private List<FieldConfigItem> configs;

    /**
     * 字段配置项
     */
    @Data
    public static class FieldConfigItem {
        /** 配置ID(更新时必传） */
        private Long configId;

        /** 字段标识 */
        @NotBlank(message = "字段标识不能为空")
        private String fieldIdentification;

        /** 字段中文名 */
        private String fieldName;

        /** 非空校验 0-否 1-是 */
        @NotNull(message = "非空校验不能为空")
        private Integer isNotNull;

        /** 是否加密字段 0-否 1-是 */
        private Integer isEncrypted;

        /** 格式校验类型 date-日期，url-URL，none-无 */
        private String formatValidationType;

        /** 日期格式 */
        private String dateFormat;

        /** URL协议 */
        private String urlProtocol;

        /** 包含数字 0-否 1-是 */
        private Integer isContentNumber;

        /** 包含汉字 0-否 1-是 */
        private Integer isContentChinese;

        /** 包含小写字母 0-否 1-是 */
        private Integer isContentLowercase;

        /** 包含大写字母 0-否 1-是 */
        private Integer isContentUppercase;

        /** 自定义正则表达式 */
        private String customRegex;

        /** 最小长度 */
        private Integer lengthMin;

        /** 最大长度 */
        private Integer lengthMax;

        /** 枚举值（逗号分隔） */
        private String enumValues;

        /** 是否启用 0-否 1-是 */
        private Integer isEnabled;
    }
}
