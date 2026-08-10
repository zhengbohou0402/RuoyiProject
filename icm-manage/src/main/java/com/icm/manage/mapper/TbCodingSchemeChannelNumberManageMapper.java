package com.icm.manage.mapper;

import com.icm.manage.domain.vo.ChannelOptionVO;
import com.icm.manage.domain.vo.OptionBusinessVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TbCodingSchemeChannelNumberManageMapper {

    /**
     * 查询渠道选项列表
     *
     * @return 渠道选项列表
     */
    @Select("SELECT channel_id AS channelId, channel_name AS channelName " +
            "FROM tb_coding_scheme_channel_manage " +
            "ORDER BY channel_name")
    List<ChannelOptionVO> selectChannelOptions();

    /**
     * 查询所有渠道号
     * @return
     */
    public List<OptionBusinessVo> selectAll();

}
