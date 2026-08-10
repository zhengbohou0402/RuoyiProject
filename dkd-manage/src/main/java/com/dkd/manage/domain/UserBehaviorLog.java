package com.dkd.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.dkd.common.annotation.Excel;
import com.dkd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户行为日志对象 tb_user_behavior_log
 * 
 * @author dkd
 * @date 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserBehaviorLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 用户手机号 */
    @Excel(name = "用户手机号")
    private String userMobile;

    /** 请求类型 */
    @Excel(name = "请求类型")
    private String eventKey;

    /** 客户端时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Excel(name = "客户端时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date clientTime;

    /** 数据类型 */
    @Excel(name = "数据类型")
    private String eventType;

    /** 服务端时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Excel(name = "服务端时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date eventTime;

    /** 设备ID */
    @Excel(name = "设备ID")
    private String deviceId;

    /** 用户会话ID */
    @Excel(name = "用户会话ID")
    private String sessionId;

    /** 平台类型 */
    @Excel(name = "平台类型")
    private String platform;

    /** 系统信息 */
    @Excel(name = "系统信息")
    private String browser;

    /** 客户端版本号 */
    @Excel(name = "客户端版本号")
    private String clientVersion;

    /** 域名 */
    @Excel(name = "域名")
    private String domain;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ip;

    /** 用户浏览器信息 */
    @Excel(name = "用户浏览器信息")
    private String userAgent;

    /** SDK版本 */
    @Excel(name = "SDK版本")
    private String sdkVersion;

    /** 数据源ID */
    @Excel(name = "数据源ID")
    private String dataSourceId;

    /** 属性(JSON格式) */
    @Excel(name = "属性")
    private String attributes;
}
