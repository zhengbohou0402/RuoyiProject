package com.icm.manage.mapper;

import com.icm.manage.domain.vo.CustomizeRuleQueryVo;

import java.util.List;
import java.util.Map;

public interface TbEasydataUserTrackinfoCompleteAllMapper {

    /**
     * 自定义规则查询-无对比时间
     *
     * @param queryVo
     * @return
     */
    public List<Map<String, Object>> queryDynamicGroupWithoutCompareTime(CustomizeRuleQueryVo queryVo);

    /**
     * 自定义规则查询-有对比时间
     *
     * @param queryVo
     * @return
     */
    public List<Map<String, Object>> queryDynamicGroupWithCompareTime(CustomizeRuleQueryVo queryVo);
}

