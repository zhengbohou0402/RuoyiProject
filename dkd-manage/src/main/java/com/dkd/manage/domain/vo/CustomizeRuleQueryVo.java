package com.dkd.manage.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomizeRuleQueryVo {

    private Long id; // 表数据id

    private Set<String> all_days;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date statisticsTimeStart;

    /**
     * 统计时间-结束
     */
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

}

