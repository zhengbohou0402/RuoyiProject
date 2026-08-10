package com.icm.manage.service;

import com.icm.manage.domain.DataLevelLabel;

import java.util.List;

/**
 * 数据等级标注Service接口
 *
 * @author ruoyi
 * @date 2025-12-16
 */
public interface IDataLevelLabelService {

    /**
     * 查询数据等级标注列表
     *
     * @param dataLevelLabel 数据等级标注
     * @return 数据等级标注集合
     */
    public List<DataLevelLabel> selectDataLevelLabelList(DataLevelLabel dataLevelLabel);

    /**
     * 新增数据等级标注
     *
     * @param dataLevelLabel 数据等级标注
     * @return 结果
     */
    public int insertDataLevelLabel(DataLevelLabel dataLevelLabel);

    /**
     * 批量检查数据等级标注唯一性
     *
     * @param input 数据等级标注对象
     * @return 数据等级标注集合
     */
    List<DataLevelLabel> checkBatchUnique(DataLevelLabel input);

    /**
     * 修改数据等级标注
     *
     * @param dataLevelLabel 数据等级标注
     * @return 结果
     */
    public int updateDataLevelLabel(DataLevelLabel dataLevelLabel);


    /**
     * 删除数据等级标注信息
     *
     * @param id 数据等级标注主键
     * @return 结果
     */
    public int deleteDataLevelLabelById(Long id);


    /**
     * 查询所有事件编码
     *
     * @return 事件编码集合
     */
    List<String> selectAllEventCodes();

    /**
     * 查询所有渠道ID
     *
     * @return 渠道ID集合
     */
    List<String> selectAllChannelIds();
}