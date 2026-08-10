package com.dkd.manage.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 渠道下拉选项VO
 *
 * @author wyq
 * @date 2025-12-23
 */
@Data
@ApiModel("渠道下拉选项")
public class ChannelOptionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 渠道ID（对应datasource_id） */
    @ApiModelProperty("渠道ID")
    private String channelId;

    /** 渠道名称 */
    @ApiModelProperty("渠道名称")
    private String channelName;
}