package com.dkd.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 自定义数据管理信息对象 tb_customize_data_manage_info
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbCustomizeDataManageInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /**
     * 规则描述
     */
    @NotBlank(message = "规则描述不能为空")
    private String ruleDesc;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 校验字段
     */
    @NotBlank(message = "校验字段不能为空")
    private String checkField;

    /**
     * 数据源ID
     */
    @NotBlank(message = "数据源ID不能为空")
    private String dataSourceId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 营销活动码
     */
    private String marketCode;

    /**
     * 页面链接
     */
    private String pageUrl;

    /**
     * 统计时间-开始
     */
    @NotNull(message = "统计时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date statisticsTimeStart;

    /**
     * 统计时间-结束
     */
    @NotNull(message = "统计时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date statisticsTimeEnd;

    /**
     * 对比时间-开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date compareTimeStart;

    /**
     * 对比时间-结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date compareTimeEnd;

    /**
     * 查询耗时 ms
     */
    private String consume_time;

    /**
     * 任务状态：0=默认，1=运行中，2=成功，3=失败
     */
    private String status;

    /**
     * 创建人id
     */
    private String create_by;

    /**
     * 修改人id
     */
    private String update_by;

    @NotNull
    private Integer pageSize;

    @NotNull
    private Integer pageNum;

    /**
     * 具体的数据内容相关
     */
    private String statistics_content;

    /**
     * clickhouse任务开始时间
     */
    private Date query_start_time;

    /**
     * clickhouse任务结束时间
     */
    private Date query_end_time;

    /**
     * 异步任务id
     */
    private String taskId;

    /**
     * 异常原因
     */
    private String error_msg;
}
