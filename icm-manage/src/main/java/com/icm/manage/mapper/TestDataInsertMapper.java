package com.icm.manage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 测试数据插入Mapper接口
 * 
 * @author icm
 * @date 2026-01-29
 */
@Mapper
public interface TestDataInsertMapper 
{
    /**
     * 插入测试数据到实时数据验证表
     *
     * @param id 主键ID
     * @param userMobile 用户手机号
     * @param eventTime 事件时间
     * @return 插入结果
     */
    int insertTestData(@Param("id") Long id,
                       @Param("userMobile") String userMobile,
                       @Param("eventTime") Date eventTime);

    /**
     * 插入测试数据到实时数据验证表（内容可变长版）
     * 除手机号/时间外，事件类型、平台、浏览器、域名、IP、UA、SDK版本、数据源ID、属性JSON
     * 全部按 id 对 5 取模循环出不同长度的内容，方便肉眼看出每条数据的差异。
     */
    int insertTestDataRich(@Param("id") Long id,
                           @Param("userMobile") String userMobile,
                           @Param("eventTime") Date eventTime,
                           @Param("eventKey") String eventKey,
                           @Param("platform") String platform,
                           @Param("browser") String browser,
                           @Param("domain") String domain,
                           @Param("ip") String ip,
                           @Param("userAgent") String userAgent,
                           @Param("sdkVersion") String sdkVersion,
                           @Param("dataSourceId") String dataSourceId,
                           @Param("attributes") String attributes);
    
    /**
     * 查询最大ID
     * 
     * @return 最大ID
     */
    Long selectMaxId();
}
