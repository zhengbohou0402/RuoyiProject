package com.dkd.manage.domain;


import com.dkd.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 数据库映射对象：只存 ID
 */
@Data
public class DataLevelLabel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** * 是否强制覆盖
     * true = 用户已确认，直接执行覆盖逻辑
     * false (默认) = 需要检查是否有重复
     */
    private boolean forceUpdate = false;

    /** 主键 */
    @Excel(name = "主键")
    private Long id;

    /** 渠道号 (对应数据库 channel_id) */
    private String channelId;

    /** 事件编码 (对应数据库 event_code) */
    private String eventCode;

    /** 渠道ID (关联键) */
    @Excel(name = "渠道ID")
    private List<String> channelIds;

    /** 事件编码 (关联键) */
    @Excel(name = "事件编码")
    private List<String> eventCodes;

    /** 数据等级 (CORE, IMPORTANT) */
    @Excel(name = "数据等级")
    private String dataLevel;

    /** 搜索值 */
    private String searchValue;

    /** 创建者 */
    @Excel(name = "创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}