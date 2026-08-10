package com.dkd.manage.mapper;

import com.dkd.manage.domain.DataLevelLabel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据等级标注Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-16
 */
public interface DataLevelLabelMapper {

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
     * 修改数据等级标注
     *
     * @param dataLevelLabel 数据等级标注
     * @return 结果
     */
    public int updateDataLevelLabel(DataLevelLabel dataLevelLabel);

    /**
     * 批量删除数据等级标注
     *
     * @param ids 需要删除的数据等级标注主键
     * @return 结果
     */

    /**
     * 删除数据等级标注信息
     *
     * @param id 数据等级标注主键
     * @return 结果
     */
    public int deleteDataLevelLabelById(Long id);

    int batchInsertDataLevelLabel(List<DataLevelLabel> batchList);

    List<DataLevelLabel> selectExistingList(
            @Param("channelIds") List<String> channelIds,
            @Param("eventCodes") List<String> eventCodes
    );

    DataLevelLabel selectDataLevelLabelById(Long id);

    // Mapper 接口

        // MyBatis 看到返回类型是 List，会自动把多行 String 组装起来
        List<String> selectAllEventCodes();

        List<String> selectAllChannelIds();

}