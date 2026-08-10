package com.icm.manage.domain;

import com.icm.common.annotation.Excel;
import com.icm.common.core.domain.BaseEntity;
import lombok.Data;

@Data
public class TbCodingSchemeChannelNumberManage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Excel(name = "主键ID")
    private Long id;

    /** 渠道号名称 */
    @Excel(name = "渠道号名称", readConverterExp = "不可重复")
    private String channelName;

    /** 渠道号 */
    @Excel(name = "渠道号")
    private String channelId;

    /** 维护时间 */
    @Excel(name = "维护时间")
    private String standTime;

    /** 所属接口 51006,51007,51010,270_csap_77011 */
    @Excel(name = "所属接口")
    private String jieKou;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contact;

    /** 联系人部门 */
    @Excel(name = "联系人部门")
    private String contactDepart;

    /** 当前用户名 */
    private String userName;

    /** 当前用户角色 */
    private String userRoleName;

    /** 需求部门 */
    @Excel(name = "需求部门")
    private String requirementDepart;

    /** 需求内容 */
    @Excel(name = "需求内容")
    private String requirementContent;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String contactPhone;

    /** 禁用 */
    @Excel(name = "禁用")
    private boolean isDisabled;

    /** 分页页码 */
    private Long pageNum;

    /** 分页大小 */
    private Long pageSize;
}
