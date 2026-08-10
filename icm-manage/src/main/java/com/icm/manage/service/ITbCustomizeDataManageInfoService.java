package com.icm.manage.service;


import com.icm.manage.domain.TbCustomizeDataManageInfo;
import com.icm.manage.domain.vo.TbCustomizeDataManageInfoInput;

import java.util.Map;

/**
 * 自定义数据管理信息Service接口
 */
public interface ITbCustomizeDataManageInfoService {

    /**
     * 查询自定义数据管理信息
     *
     * @param id 自定义数据管理信息主键
     * @return 自定义数据管理信息
     */
    public Map<String, Object> selectTbCustomizeDataManageInfoById(Long id);

    /**
     * 查询自定义数据管理信息列表
     *
     * @param tbCustomizeDataManageInfo 自定义数据管理信息
     * @return 自定义数据管理信息集合
     */
    public Map<String, Object> selectTbCustomizeDataManageInfoList(
            TbCustomizeDataManageInfo tbCustomizeDataManageInfo);

    /**
     * 新增自定义数据管理信息
     *
     * @param tbCustomizeDataManageInfo 自定义数据管理信息
     * @return 结果
     */
    public Map<String, Object> insertTbCustomizeDataManageInfo(
            TbCustomizeDataManageInfoInput tbCustomizeDataManageInfo);

    /**
     * 修改自定义数据管理信息
     *
     * @param tbCustomizeDataManageInfo 自定义数据管理信息
     * @return 结果
     */
    public Map<String, Object> updateTbCustomizeDataManageInfo(
            TbCustomizeDataManageInfo tbCustomizeDataManageInfo);

    /**
     * 删除自定义数据管理信息
     *
     * @param id 自定义数据管理信息主键
     * @return 结果
     */
    public Map<String, Object> deleteTbCustomizeDataManageInfoById(Long id);

    /**
     * 启动查询任务
     *
     * @param id
     * @return
     */
    public Map<String, Object> startTask(Long id);

    /**
     * 停止查询任务
     *
     * @param id
     * @return
     */
    public Map<String, Object> stopTask(Long id);

    /**
     * 查询所有渠道号
     */
    public Map<String, Object> queryChannelList();
}
