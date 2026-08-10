package com.icm.manage.service.impl;

import com.icm.common.utils.SecurityUtils;
import com.icm.manage.domain.TbFieldValidationConfig;
import com.icm.manage.domain.dto.ConfigSaveDTO;
import com.icm.manage.domain.vo.FieldConfigVO;
import com.icm.manage.mapper.TbCodingSchemeFieldMaintenanceMapper;
import com.icm.manage.mapper.TbFieldValidationConfigMapper;
import com.icm.manage.service.ITbFieldValidationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 字段校验规则配置Service实现类
 *
 * @author wangyaqi1
 */
@Service
public class TbFieldValidationConfigServiceImpl implements ITbFieldValidationConfigService {

    @Autowired
    private TbFieldValidationConfigMapper configMapper;

    @Autowired
    private TbCodingSchemeFieldMaintenanceMapper fieldMaintenanceMapper;

    // @Autowired
    // private ITbFieldValidationConfigSnapshotService snapshotService; // 旧代码，已废弃

    /**
     * 查询字段列表（含规则配置）
     * 从元数据表查询所有字段，合并规则配置信息
     */
    @Override
    public List<FieldConfigVO> selectFieldListWithConfig(String datasourceId) {
        // 直接调用Mapper的LEFT JOIN查询
        // SQL已经完成了字段元数据和规则配置的关联
        return fieldMaintenanceMapper.selectFieldListWithConfig(datasourceId);
    }

    /**
     * 保存规则配置（批量）
     * 整体对整体关系：多个数据源共享同一套规则
     * 1. 如果已有配置，先创建快照备份（BEFORE_UPDATE）
     * 2. 保存或更新规则配置
     */
    @Override
    @Transactional
    public Map<String, Object> saveConfigs(ConfigSaveDTO configSaveDTO) {
        Map<String, Object> result = new HashMap<>();
        List<Long> snapshotIds = new ArrayList<>();
        int insertCount = 0;
        int updateCount = 0;
        List<String> successFields = new ArrayList<>();
        List<String> failedFields = new ArrayList<>();

        // 获取操作人，未登录时使用默认值
        String operateBy;
        try {
            operateBy = SecurityUtils.getUsername();
        } catch (Exception e) {
            operateBy = "system"; // 未登录时使用system
        }

        // tableName默认值
        String tableName = configSaveDTO.getTableName() != null && !configSaveDTO.getTableName().isEmpty()
                ? configSaveDTO.getTableName()
                : "tb_easydata_user_trackinginfo_complete_all";

        // 将多个datasourceId拼接成逗号分隔的字符串
        String datasourceIdStr = String.join(",", configSaveDTO.getDatasourceIds());

        // 按字段循环处理（整体对整体关系）
        for (ConfigSaveDTO.FieldConfigItem item : configSaveDTO.getConfigs()) {
            try {
                // 0. 规则互斥校验
                validateRuleMutex(item);

                // 1. 查询是否已有配置（只按字段标识查询）
                TbFieldValidationConfig existingConfig = configMapper.selectByFieldIdentification(
                        item.getFieldIdentification()
                );

                // 2. 如果已有配置，先创建快照备份
                // 注释：快照功能现在在定时任务执行前统一备份，这里不需要单独备份
                // if (existingConfig != null) {
                //     Long snapshotId = snapshotService.createSnapshot(existingConfig, "BEFORE_UPDATE", operateBy);
                //     snapshotIds.add(snapshotId);
                // }

                // 3. 构建配置对象
                TbFieldValidationConfig config = new TbFieldValidationConfig();
                if (existingConfig != null) {
                    config.setId(existingConfig.getId());
                    config.setUpdateBy(operateBy);
                    config.setUpdateTime(new Date());
                } else {
                    config.setCreateBy(operateBy);
                    config.setCreateTime(new Date());
                }

                // 复制基本信息（datasourceId是拼接后的字符串）
                config.setDatasourceId(datasourceIdStr);
                config.setTableName(tableName);
                config.setSampleCount(configSaveDTO.getSampleCount());
                config.setFieldIdentification(item.getFieldIdentification());
                config.setFieldName(item.getFieldName());

                // 复制规则配置
                config.setIsNotNull(item.getIsNotNull());
                config.setIsEncrypted(item.getIsEncrypted() != null ? item.getIsEncrypted() : 0);
                config.setFormatValidationType(item.getFormatValidationType());
                config.setDateFormat(item.getDateFormat());
                config.setUrlProtocol(item.getUrlProtocol());
                config.setIsContentNumber(item.getIsContentNumber());
                config.setIsContentChinese(item.getIsContentChinese());
                config.setIsContentLowercase(item.getIsContentLowercase());
                config.setIsContentUppercase(item.getIsContentUppercase());
                config.setCustomRegex(item.getCustomRegex());
                config.setLengthMin(item.getLengthMin());
                config.setLengthMax(item.getLengthMax());
                config.setEnumValues(item.getEnumValues());
                config.setIsEnabled(item.getIsEnabled() != null ? item.getIsEnabled() : 1);

                // 4. 保存或更新配置
                if (existingConfig != null) {
                    configMapper.updateTbFieldValidationConfig(config);
                    updateCount++;
                } else {
                    configMapper.insertTbFieldValidationConfig(config);
                    insertCount++;
                }

                // 记录成功字段
                successFields.add(item.getFieldIdentification());

            } catch (Exception e) {
                // 记录失败字段和错误原因
                failedFields.add(item.getFieldIdentification() + ": " + e.getMessage());
                e.printStackTrace(); // 临时打印异常堆栈，方便调试
            }
        }

        // 构建返回结果
        result.put("datasourceCount", configSaveDTO.getDatasourceIds().size());
        result.put("datasourceIds", datasourceIdStr);
        result.put("insertCount", insertCount);
        result.put("updateCount", updateCount);
        result.put("snapshotIds", snapshotIds);
        result.put("successFields", successFields);
        result.put("failedFields", failedFields);

        return result;
    }

    /**
     * 根据ID查询字段校验规则配置
     */
    @Override
    public TbFieldValidationConfig selectTbFieldValidationConfigById(Long id) {
        return configMapper.selectTbFieldValidationConfigById(id);
    }

    /**
     * 查询字段校验规则配置列表
     */
    @Override
    public List<TbFieldValidationConfig> selectTbFieldValidationConfigList(TbFieldValidationConfig tbFieldValidationConfig) {
        return configMapper.selectTbFieldValidationConfigList(tbFieldValidationConfig);
    }

    /**
     * 根据数据源ID和字段标识查询配置
     */
    @Override
    public TbFieldValidationConfig selectByDatasourceAndField(String datasourceId, String fieldIdentification) {
        return configMapper.selectByDatasourceAndField(datasourceId, fieldIdentification);
    }

    /**
     * 新增字段校验规则配置
     */
    @Override
    public int insertTbFieldValidationConfig(TbFieldValidationConfig tbFieldValidationConfig) {
        return configMapper.insertTbFieldValidationConfig(tbFieldValidationConfig);
    }

    /**
     * 修改字段校验规则配置
     */
    @Override
    public int updateTbFieldValidationConfig(TbFieldValidationConfig tbFieldValidationConfig) {
        return configMapper.updateTbFieldValidationConfig(tbFieldValidationConfig);
    }

    /**
     * 查询字段校验规则配置
     */

    /**
     * 批量删除字段校验规则配置
     */
    @Override
    public int deleteTbFieldValidationConfigByIds(Long[] ids) {
        return configMapper.deleteTbFieldValidationConfigByIds(ids);
    }

    /**
     * 删除字段校验规则配置信息
     */
    @Override
    public int deleteTbFieldValidationConfigById(Long id) {
        return configMapper.deleteTbFieldValidationConfigById(id);
    }

    /**
     * 校验规则互斥关系
     * 1. 非空选“否”时，其他所有规则不可配置
     * 2. 格式校验、正则校验、范围校验三选一
     */
    private void validateRuleMutex(ConfigSaveDTO.FieldConfigItem item) {
        String fieldId = item.getFieldIdentification();

        // 规则0：加密字段勾选时，所有校验规则都无效
        if (item.getIsEncrypted() != null && item.getIsEncrypted() == 1) {
            // 检查是否配置了其他规则
            boolean hasOtherRules = false;
            StringBuilder ruleNames = new StringBuilder();

            // 检查格式校验
            if (item.getFormatValidationType() != null && !item.getFormatValidationType().equals("none")) {
                hasOtherRules = true;
                ruleNames.append("格式校验、");
            }

            // 检查正则校验
            if ((item.getIsContentNumber() != null && item.getIsContentNumber() == 1) ||
                    (item.getIsContentChinese() != null && item.getIsContentChinese() == 1) ||
                    (item.getIsContentLowercase() != null && item.getIsContentLowercase() == 1) ||
                    (item.getIsContentUppercase() != null && item.getIsContentUppercase() == 1) ||
                    (item.getCustomRegex() != null && !item.getCustomRegex().isEmpty())) {
                hasOtherRules = true;
                ruleNames.append("正则校验、");
            }

            // 检查范围校验
            if ((item.getLengthMin() != null || item.getLengthMax() != null) ||
                    (item.getEnumValues() != null && !item.getEnumValues().isEmpty())) {
                hasOtherRules = true;
                ruleNames.append("范围校验、");
            }

            if (hasOtherRules) {
                throw new RuntimeException(String.format(
                        "字段 [%s] 选择加密字段后，所有校验规则无效，不允许配置：%s",
                        fieldId,
                        ruleNames.toString().replaceAll("、$", "")
                ));
            }

            // 加密字段直接返回，无需后续校验
            return;
        }

        // 规则1：非空选"否"时，不允许配置其他任何规则
        if (item.getIsNotNull() != null && item.getIsNotNull() == 0) {
            // 检查是否配置了其他规则
            boolean hasOtherRules = false;
            StringBuilder ruleNames = new StringBuilder();

            // 检查格式校验
            if (item.getFormatValidationType() != null && !item.getFormatValidationType().equals("none")) {
                hasOtherRules = true;
                ruleNames.append("格式校验、");
            }

            // 检查正则校验
            if ((item.getIsContentNumber() != null && item.getIsContentNumber() == 1) ||
                    (item.getIsContentChinese() != null && item.getIsContentChinese() == 1) ||
                    (item.getIsContentLowercase() != null && item.getIsContentLowercase() == 1) ||
                    (item.getIsContentUppercase() != null && item.getIsContentUppercase() == 1) ||
                    (item.getCustomRegex() != null && !item.getCustomRegex().isEmpty())) {
                hasOtherRules = true;
                ruleNames.append("正则校验、");
            }

            // 检查范围校验
            if ((item.getLengthMin() != null || item.getLengthMax() != null) ||
                    (item.getEnumValues() != null && !item.getEnumValues().isEmpty())) {
                hasOtherRules = true;
                ruleNames.append("范围校验、");
            }

            if (hasOtherRules) {
                throw new RuntimeException(String.format(
                        "字段 [%s] 非空校验选择\"否\"时，不允许配置其他规则，当前配置了：%s",
                        fieldId,
                        ruleNames.toString().replaceAll("、$", "")
                ));
            }

            // 非空为"否"时，直接返回，无需后续校验
            return;
        }

        // 规则2：格式校验、正则校验、范围校验 三选一
        int ruleTypeCount = 0;
        StringBuilder configuredTypes = new StringBuilder();

        // 检查格式校验
        boolean hasFormatValidation = item.getFormatValidationType() != null
                && !item.getFormatValidationType().equals("none");
        if (hasFormatValidation) {
            ruleTypeCount++;
            configuredTypes.append("格式校验、");
        }

        // 检查正则校验
        boolean hasRegexValidation = (item.getIsContentNumber() != null && item.getIsContentNumber() == 1) ||
                (item.getIsContentChinese() != null && item.getIsContentChinese() == 1) ||
                (item.getIsContentLowercase() != null && item.getIsContentLowercase() == 1) ||
                (item.getIsContentUppercase() != null && item.getIsContentUppercase() == 1) ||
                (item.getCustomRegex() != null && !item.getCustomRegex().isEmpty());
        if (hasRegexValidation) {
            ruleTypeCount++;
            configuredTypes.append("正则校验、");
        }

        // 检查范围校验
        boolean hasRangeValidation = (item.getLengthMin() != null || item.getLengthMax() != null) ||
                (item.getEnumValues() != null && !item.getEnumValues().isEmpty());
        if (hasRangeValidation) {
            ruleTypeCount++;
            configuredTypes.append("范围校验、");
        }

        // 只能配置一种类型的校验
        if (ruleTypeCount > 1) {
            throw new RuntimeException(String.format(
                    "字段[%s] 格式校验、正则校验、范围校验只能选择一种，当前配置了：%s",
                    fieldId,
                    configuredTypes.toString().replaceAll("、$", "")
            ));
        }
    }

}
