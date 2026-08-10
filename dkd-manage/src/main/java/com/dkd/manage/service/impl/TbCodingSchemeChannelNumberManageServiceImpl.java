package com.dkd.manage.service.impl;

import com.dkd.manage.domain.vo.ChannelOptionVO;
import com.dkd.manage.mapper.TbCodingSchemeChannelNumberManageMapper;
import com.dkd.manage.service.ITbCodingSchemeChannelNumberManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 渠道编码方案管理服务实现类
 *
 * @author wangyaqi1, lingpkm
 */
@Service
public class TbCodingSchemeChannelNumberManageServiceImpl implements ITbCodingSchemeChannelNumberManageService {

    @Autowired
    private TbCodingSchemeChannelNumberManageMapper tbCodingSchemeChannelNumberManageMapper;

    /**
     * 查询渠道下拉选项
     *
     * @return 渠道选项列表
     */
    @Override
    public List<ChannelOptionVO> selectChannelOptions() {
        return tbCodingSchemeChannelNumberManageMapper.selectChannelOptions();
    }
}
