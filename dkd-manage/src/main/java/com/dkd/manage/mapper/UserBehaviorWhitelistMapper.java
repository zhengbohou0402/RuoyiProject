package com.dkd.manage.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 用户手机号白名单 Mapper
 */
public interface UserBehaviorWhitelistMapper {

    /**
     * 统计白名单中是否存在该手机号（存在返回 >0）
     */
    int countByPhone(@Param("phone") String phone);
}


