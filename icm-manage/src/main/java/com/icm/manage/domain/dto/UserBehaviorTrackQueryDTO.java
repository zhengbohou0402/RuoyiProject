package com.icm.manage.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为轨迹查询参数（前端表单）
 */
@Data
public class UserBehaviorTrackQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 手机号（必填）
     */
    private String phone;

    /**
     * 开始时间（必填，精确到秒）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    /**
     * 结束时间（必填，精确到秒）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 当前页码（默认1）
     */
    private Integer pageNum = 1;

    /**
     * 每页条数（默认10）
     */
    private Integer pageSize = 10;



    /**
     * 获取分页偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}


