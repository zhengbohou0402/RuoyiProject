package com.dkd.manage.service;

import com.dkd.manage.domain.vo.ChannelOptionVO;

import java.util.List;

public interface ITbCodingSchemeChannelNumberManageService {
    /**
     * 查询渠道下拉选项
     *
     * @return 渠道选项列表
     */
    List<ChannelOptionVO> selectChannelOptions();
}
