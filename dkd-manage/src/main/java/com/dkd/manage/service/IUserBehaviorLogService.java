package com.dkd.manage.service;

import com.dkd.manage.domain.UserBehaviorLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户行为日志Service接口
 * 
 * @author dkd
 * @date 2026-01-27
 */
public interface IUserBehaviorLogService 
{
    /**
     * 查询用户行为日志
     * 
     * @param id 用户行为日志主键
     * @return 用户行为日志
     */
    public UserBehaviorLog selectUserBehaviorLogById(Long id);

    /**
     * 查询用户行为日志列表
     * 
     * @param userBehaviorLog 用户行为日志
     * @return 用户行为日志集合
     */
    public List<UserBehaviorLog> selectUserBehaviorLogList(UserBehaviorLog userBehaviorLog);

    /**
     * 查询增量数据(用于模拟长链接)
     * 
     * @param userMobile 用户手机号
     * @param queryTime 查询时间
     * @return Map包含dataList和queryEndTime
     */
    public Map<String, Object> selectIncrementalData(String userMobile, Date queryTime);

    /**
     * 新增用户行为日志
     * 
     * @param userBehaviorLog 用户行为日志
     * @return 结果
     */
    public int insertUserBehaviorLog(UserBehaviorLog userBehaviorLog);

    /**
     * 修改用户行为日志
     * 
     * @param userBehaviorLog 用户行为日志
     * @return 结果
     */
    public int updateUserBehaviorLog(UserBehaviorLog userBehaviorLog);

    /**
     * 批量删除用户行为日志
     * 
     * @param ids 需要删除的用户行为日志主键集合
     * @return 结果
     */
    public int deleteUserBehaviorLogByIds(Long[] ids);

    /**
     * 删除用户行为日志信息
     * 
     * @param id 用户行为日志主键
     * @return 结果
     */
    public int deleteUserBehaviorLogById(Long id);
}
