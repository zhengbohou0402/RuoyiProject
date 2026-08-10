package com.dkd.manage.mapper;

import com.dkd.manage.domain.UserBehaviorLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户行为日志Mapper接口
 * 
 * @author dkd
 * @date 2026-01-27
 */
public interface UserBehaviorLogMapper 
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
     * 根据手机号和时间查询增量数据
     * 
     * @param userMobile 用户手机号
     * @param queryTime 查询时间(查询此时间之后的数据)
     * @return 用户行为日志集合
     */
    public List<UserBehaviorLog> selectIncrementalDataByMobile(@Param("userMobile") String userMobile, 
                                                                 @Param("queryTime") Date queryTime);

    /**
     * 查询最大的event_time(用于下次增量查询)
     * 
     * 【设计思路】
     * 1. 使用数据本身的最大时间而非当前系统时间，防止数据延迟入库导致漏数据
     * 2. 有数据时返回max(event_time)，确保下次查询能接上最后一条数据
     * 3. 无数据时返回queryTime保持不变，避免时间跳跃
     * 4. 首次查询(queryTime=null)返回now(3)作为基准时间
     * 
     * 【为什么不用系统时间？】
     * 场景：查询时间19:48:59，有条数据event_time=12:00:25但在19:49:00才入库
     * - 用系统时间：queryEndTime=19:48:59，下次查询>19:48:59，漏掉12:00:25
     * - 用最大时间：queryEndTime=12:00:20(上条最大)，下次查询>12:00:20，能查到12:00:25
     * 
     * @param userMobile 用户手机号
     * @param queryTime 上次查询时间
     * @return 最大的event_time，如果没数据则返回queryTime或当前时间
     */
    public Date selectMaxEventTime(@Param("userMobile") String userMobile, 
                                    @Param("queryTime") Date queryTime);

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
     * 删除用户行为日志
     * 
     * @param id 用户行为日志主键
     * @return 结果
     */
    public int deleteUserBehaviorLogById(Long id);

    /**
     * 批量删除用户行为日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserBehaviorLogByIds(Long[] ids);
}
