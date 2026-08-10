package com.dkd.manage.domain.vo;

import com.dkd.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为轨迹返回/导出 VO（字段与前端展示、Excel 导出保持一致）
 *
 * 面试踩坑点：原来这几个字段上都加了 @JsonProperty("client_time")/@JsonProperty("WT_et") 之类的
 * 注解，强行把 JSON 输出字段名改成了下划线/大写风格。但前端表格 <el-table-column prop="clientTime">
 * / prop="wtEt" 用的是驼峰命名，两边完全对不上——只有 userId 因为改完之后碰巧还是 "userId"
 * 才侥幸能显示，其余列全部渲染成空白。这是个"接口测了返回200、数据也对，但页面死活不显示"的
 * 典型隐蔽 bug：Postman/curl 里看 JSON 一切正常，只有真的点开页面才会发现。
 * 去掉这几个 @JsonProperty，让 Jackson 走默认的驼峰序列化，跟前端字段名保持一致。
 */
@Data
public class TbUserBehaviorTrackVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date clientTime;

    /**
     * 手机号
     */
    @Excel(name = "手机号")
    private String userId;

    /**
     * 事件类型
     */
    @Excel(name = "事件类型")
    private String wtEt;

    /**
     * 页面名称
     */
    @Excel(name = "页面名称")
    private String wtTi;

    /**
     * 页面链接
     */
    @Excel(name = "页面链接")
    private String wtEs;

    /**
     * 点位内容
     */
    @Excel(name = "点位内容")
    private String wtEnvName;

    /**
     * 事件编码
     */
    @Excel(name = "事件编码")
    private String wtEvent;
}


