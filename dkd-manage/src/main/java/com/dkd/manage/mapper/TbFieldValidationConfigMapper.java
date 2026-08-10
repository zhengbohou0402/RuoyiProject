package com.dkd.manage.mapper;

import com.dkd.manage.domain.TbFieldValidationConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段校验规则配置Mapper接口
 *
 * @author wyq
 * @date 2025-12-22
 */
public interface TbFieldValidationConfigMapper {

    /**
     * 查询字段校验规则配置
     *
     * @param id 字段校验规则配置主键
     * @return 字段校验规则配置
     */
    TbFieldValidationConfig selectTbFieldValidationConfigById(Long id);

    /**
     * 查询字段校验规则配置列表
     *
     * @param tbFieldValidationConfig 字段校验规则配置
     * @return 字段校验规则配置集合
     */
    List<TbFieldValidationConfig> selectTbFieldValidationConfigList(TbFieldValidationConfig tbFieldValidationConfig);

    /**
     * 根据数据源ID和字段标识查询配置（已废弃，整体对整体关系后不再使用）
     *
     * @param datasourceId 数据源标识
     * @param fieldIdentification 字段标识
     * @return 字段校验规则配置
     */
    @Deprecated
    TbFieldValidationConfig selectByDatasourceAndField(
            @Param("datasourceId") String datasourceId,
            @Param("fieldIdentification") String fieldIdentification
    );

    /**
     * 根据字段标识查询配置
     *
     * @param fieldIdentification 字段标识
     * @return 字段校验规则配置
     */
    TbFieldValidationConfig selectByFieldIdentification(@Param("fieldIdentification") String fieldIdentification);

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
     * 删除字段校验规则配置
     *
     * @param id 字段校验规则配置主键
     * @return 结果
     */
    int deleteTbFieldValidationConfigById(Long id);

    /**
     * 批量删除字段校验规则配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteTbFieldValidationConfigByIds(Long[] ids);
}
