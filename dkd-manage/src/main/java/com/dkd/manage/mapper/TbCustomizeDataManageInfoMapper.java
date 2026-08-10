package com.dkd.manage.mapper;

import com.dkd.manage.domain.TbCustomizeDataManageInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 自定义规则管理Mapper接口
 *
 * @author
 * @date 2023-12-22
 */
@Mapper
public interface TbCustomizeDataManageInfoMapper {

    /**
     * 查询自定义规则管理
     *
     * @param id 自定义规则管理主键
     * @return 自定义规则管理
     */
    public TbCustomizeDataManageInfo selectTbCustomizeDataManageInfoById(Long id);

    /**
     * 查询自定义规则管理列表
     *
     * @param tbCustomizeDataManageInfo 自定义规则管理
     * @return 自定义规则管理集合
     */
    public List<TbCustomizeDataManageInfo> selectTbCustomizeDataManageInfoList(TbCustomizeDataManageInfo tbCustomizeDataManageInfo);

    /**
     * 新增自定义规则管理
     *
     * @param tbCustomizeDataManageInfo 自定义规则管理
     * @return 结果
     */
    public int insertTbCustomizeDataManageInfo(TbCustomizeDataManageInfo tbCustomizeDataManageInfo);

    /**
     * 修改自定义规则管理
     *
     * @param tbCustomizeDataManageInfo 自定义规则管理
     * @return 结果
     */
    public int updateTbCustomizeDataManageInfo(TbCustomizeDataManageInfo tbCustomizeDataManageInfo);

    /**
     * 删除自定义规则管理
     *
     * @param id 自定义规则管理主键
     * @return 结果
     */
    public int deleteTbCustomizeDataManageInfoById(Long id);

    /**
     * 将指定id的数据状态statusOld修改为statusNew
     *
     * @return 结果
     */
    public int updateTbCustomizeDataStatus(TbCustomizeDataManageInfo info);
}
