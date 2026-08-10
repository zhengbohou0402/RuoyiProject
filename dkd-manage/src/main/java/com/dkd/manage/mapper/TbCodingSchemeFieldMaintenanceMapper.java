package com.dkd.manage.mapper;

import com.dkd.manage.domain.vo.FieldConfigVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 插码管理方案-字段维护Mapper接口
 *
 * @author yxp
 * @date 2024-08-09
 */
public interface TbCodingSchemeFieldMaintenanceMapper {

    /**
     * 查询字段列表（含规则配置）
     * 查询逻辑：
     * 1. 从元数据表查询所有字段
     * 2. LEFT JOIN 规则配置表，获取已配置的规则
     * 3. 返回字段+规则的组合VO
     *
     * @param datasourceId 数据源标识（可选过滤）
     * @return 字段配置VO列表
     */
    List<FieldConfigVO> selectFieldListWithConfig(@Param("datasourceId") String datasourceId);
}
