package com.icm.manage.service.impl;

import com.alibaba.fastjson2.JSON;
import com.icm.common.utils.DateUtils;
import com.icm.manage.domain.UserBehaviorLog;
import com.icm.manage.mapper.UserBehaviorLogMapper;
import com.icm.manage.service.IUserBehaviorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户行为日志Service业务层处理
 * 
 * @author icm
 * @date 2026-01-27
 */
@Service
public class UserBehaviorLogServiceImpl implements IUserBehaviorLogService 
{
    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;

    /**
     * 查询用户行为日志
     * 
     * @param id 用户行为日志主键
     * @return 用户行为日志
     */
    @Override
    public UserBehaviorLog selectUserBehaviorLogById(Long id)
    {
        return userBehaviorLogMapper.selectUserBehaviorLogById(id);
    }

    /**
     * 查询用户行为日志列表
     * 
     * @param userBehaviorLog 用户行为日志
     * @return 用户行为日志
     */
    @Override
    public List<UserBehaviorLog> selectUserBehaviorLogList(UserBehaviorLog userBehaviorLog)
    {
        return userBehaviorLogMapper.selectUserBehaviorLogList(userBehaviorLog);
    }

    /**
     * 查询增量数据(用于模拟长链接)
     * 
     * 【核心设计】
     * 1. 前端轮询(3秒间隔)，每次传上次的queryEndTime
     * 2. 后端查询event_time > queryTime的数据(增量查询)
     * 3. 返回max(event_time)作为下次查询基准，防止数据延迟入库漏查
     * 
     * 【为什么用轮询不用WebSocket？】
     * - 简单可靠，无需维护长连接
     * - 服务器资源占用少
     * - 3秒延迟对日志查看场景可接受
     * 
     * @param userMobile 用户手机号
     * @param queryTime 上次查询时间(首次为null)
     * @return Map包含dataList(数据列表)和queryEndTime(下次查询时间)
     */
    @Override
    public Map<String, Object> selectIncrementalData(String userMobile, Date queryTime)
    {
        // 查询增量数据(event_time > queryTime)
        List<UserBehaviorLog> logList = userBehaviorLogMapper.selectIncrementalDataByMobile(
            userMobile, queryTime);
        
        // 查询下次查询时间(有数据返回最大event_time，没数据返回queryTime或当前时间)
        // 关键：用数据的最大时间而非系统时间，防止延迟入库导致漏数据
        Date nextQueryTime = userBehaviorLogMapper.selectMaxEventTime(userMobile, queryTime);
        
        // 拼装返回数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        for (UserBehaviorLog log : logList) {
            Map<String, Object> item = new HashMap<>();
            
            // 时间字段
            item.put("time", log.getEventTime() != null ? sdf.format(log.getEventTime()) : null);
            
            // 数据内容（Map会自动转成JSON对象，不包含id等敏感字段）
            Map<String, Object> dataContent = new HashMap<>();
            dataContent.put("event_key", log.getEventKey());
            dataContent.put("client_time", log.getClientTime() != null ? sdf.format(log.getClientTime()) : null);
            dataContent.put("event_type", log.getEventType());
            dataContent.put("user_mobile", log.getUserMobile());
            dataContent.put("deviceid", log.getDeviceId());
            dataContent.put("session_id", log.getSessionId());
            dataContent.put("platform", log.getPlatform());
            dataContent.put("browser", log.getBrowser());
            dataContent.put("client_version", log.getClientVersion());
            dataContent.put("domain", log.getDomain());
            dataContent.put("ip", log.getIp());
            dataContent.put("user_agent", log.getUserAgent());
            dataContent.put("sdk_version", log.getSdkVersion());
            dataContent.put("data_source_id", log.getDataSourceId());
            // 在循环里，把 attributes 解析成对象再放入
            if (log.getAttributes() != null && !log.getAttributes().isEmpty()) {
                try {
                    // 把 JSON 字符串解析成 Object（可能是 Map 或 List）
                    Object attributesObj = JSON.parseObject(log.getAttributes(), Object.class);
                    dataContent.put("attributes", attributesObj);
                } catch (Exception e) {
                    // 解析失败就原样放入
                    dataContent.put("attributes", log.getAttributes());
                }
            } else {
                dataContent.put("attributes", null);
            }
            // 用数组包裹
            String dataContentJson = "[" + JSON.toJSONString(dataContent) + "]";
            item.put("dataContent", dataContentJson);
            dataList.add(item);
        }
        
        // 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("dataList", dataList);
        // 返回 epoch 毫秒数（而不是格式化字符串），前端下次请求原样传回。
        // 用数值传递不经过字符串格式化/解析，避免时区解析问题（同 UserBehaviorTrackServiceImpl 的思路）
        result.put("queryEndTime", nextQueryTime.getTime());

        return result;
    }

    /**
     * 新增用户行为日志
     * 
     * @param userBehaviorLog 用户行为日志
     * @return 结果
     */
    @Override
    public int insertUserBehaviorLog(UserBehaviorLog userBehaviorLog)
    {
        userBehaviorLog.setCreateTime(DateUtils.getNowDate());
        return userBehaviorLogMapper.insertUserBehaviorLog(userBehaviorLog);
    }

    /**
     * 修改用户行为日志
     * 
     * @param userBehaviorLog 用户行为日志
     * @return 结果
     */
    @Override
    public int updateUserBehaviorLog(UserBehaviorLog userBehaviorLog)
    {
        return userBehaviorLogMapper.updateUserBehaviorLog(userBehaviorLog);
    }

    /**
     * 批量删除用户行为日志
     * 
     * @param ids 需要删除的用户行为日志主键
     * @return 结果
     */
    @Override
    public int deleteUserBehaviorLogByIds(Long[] ids)
    {
        return userBehaviorLogMapper.deleteUserBehaviorLogByIds(ids);
    }

    /**
     * 删除用户行为日志信息
     * 
     * @param id 用户行为日志主键
     * @return 结果
     */
    @Override
    public int deleteUserBehaviorLogById(Long id)
    {
        return userBehaviorLogMapper.deleteUserBehaviorLogById(id);
    }
}
