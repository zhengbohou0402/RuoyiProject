package com.dkd.manage.service;

import com.dkd.manage.domain.TbFieldValidationConfig;
import com.dkd.manage.domain.dto.ConfigSaveDTO;
import com.dkd.manage.domain.vo.FieldConfigVO;

import java.util.List;
import java.util.Map;

/**
 * 字段校验规则配置Service接口
 *
 * @author wyq
 * @date 2025-12-22
 */
public interface ITbFieldValidationConfigService {
    /**
     * 查询字段列表（含规则配置）
     * 从元数据表查询所有字段，合并规则配置信息
     *
     * @param datasourceId 数据源标识
     * @return 字段配置列表
     */
    List<FieldConfigVO> selectFieldListWithConfig(String datasourceId);

    /**
     * 查询字段校验规则配置
     *
     * @param id 字段校验规则配置主键
     * @return 字段校验规则配置
     */
    TbFieldValidationConfig selectTbFieldValidationConfigById(Long id);

    List<TbFieldValidationConfig> selectTbFieldValidationConfigList(TbFieldValidationConfig tbFieldValidationConfig);

    /**
     * 根据数据源ID和字段标识查询配置
     *
     * @param datasourceId 数据源标识
     * @param fieldIdentification 字段标识
     * @return 字段校验规则配置
     */
    TbFieldValidationConfig selectByDatasourceAndField(String datasourceId, String fieldIdentification);

    /**
     * 保存规则配置（批量）
     * 1. 如果已有配置，先创建快照备份(BEFORE_UPDATE)
     * 2. 保存或更新规则配置
     *
     * @param configSaveDTO 保存规则DTO
     * @return 保存结果（包含保存数量和快照ID）
     */
    Map<String, Object> saveConfigs(ConfigSaveDTO configSaveDTO);

    /**
     * 新增字段校验规则配置
     *
     * @param tbFieldValidationConfig 字段校验规则配置
     * @return 结果
     */
    int insertTbFieldValidationConfig(TbFieldValidationConfig tbFieldValidationConfig);

    /**
     * 修改字段校验规则配置
     *
     * @param tbFieldValidationConfig 字段校验规则配置
     * @return 结果
     */
    int updateTbFieldValidationConfig(TbFieldValidationConfig tbFieldValidationConfig);

    /**
     * 批量删除字段校验规则配置
     *
     * @param ids 需要删除的字段校验规则配置主键集合
     * @return 结果
     */
    int deleteTbFieldValidationConfigByIds(Long[] ids);

    /**
     * 删除字段校验规则配置信息
     *
     * @param id 字段校验规则配置主键
     * @return 结果
     */
    int deleteTbFieldValidationConfigById(Long id);
}
