package com.icm.manage.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.icm.common.core.domain.entity.SysUser;
import com.icm.common.utils.SecurityUtils;
import com.icm.common.utils.StringUtils;
import com.icm.manage.domain.TbCustomizeDataManageInfo;
import com.icm.manage.domain.vo.CustomizeRuleQueryVo;
import com.icm.manage.domain.vo.OptionBusinessVo;
import com.icm.manage.domain.vo.TbCustomizeDataManageInfoInput;
import com.icm.manage.mapper.TbCodingSchemeChannelNumberManageMapper;

import com.icm.manage.mapper.TbEasydataUserTrackinfoCompleteAllMapper;

import com.icm.manage.mapper.TbCustomizeDataManageInfoMapper;
import com.icm.manage.service.ITbCustomizeDataManageInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 自定义规则管理服务实现类
 */
@Slf4j
@Service
public class TbCustomizeDataManageInfoServiceImpl implements ITbCustomizeDataManageInfoService {

    @Autowired
    private TbCustomizeDataManageInfoMapper tbCustomizeDataManageInfoMapper;
    @Autowired
    private TbCodingSchemeChannelNumberManageMapper tbCodingSchemeChannelNumberManageMapper;

    // clickHouseSqlSessionFactory 目前还没有对应的 Bean 配置（ClickHouse 统计任务功能尚未完成），
    // 改成非必需注入，避免整个应用因为这一个 Bean 缺失而无法启动；真正调用 startTask() 时如果这里是 null 会报错，
    // 需要等 ClickHouse 环境和对应的 SqlSessionFactory Bean 配置好之后才能正常工作。
    @Autowired(required = false)
    @Qualifier("clickHouseSqlSessionFactory")
    private SqlSessionFactory clickHouseSqlSessionFactory;

    private final ConcurrentHashMap<String, SqlSession> queryContexts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Thread> taskThread = new ConcurrentHashMap<>();

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 查询自定义规则管理
     *
     * @param id 自定义规则管理主键
     */
    @Override
    public Map<String, Object> selectTbCustomizeDataManageInfoById(Long id) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "查询成功");

        TbCustomizeDataManageInfo tbCustomizeDataManageInfo =
                tbCustomizeDataManageInfoMapper.selectTbCustomizeDataManageInfoById(id);

        if (null == tbCustomizeDataManageInfo) {
            map.put("code", 1001);
            map.put("msg", "数据不存在");
            return map;
        }

        map.put("ruleName", tbCustomizeDataManageInfo.getRuleName());
        map.put("ruleDesc", tbCustomizeDataManageInfo.getRuleDesc());
        map.put("creator", tbCustomizeDataManageInfo.getCreator());
        map.put("creatime", tbCustomizeDataManageInfo.getCreateTime());
        map.put("checkField", tbCustomizeDataManageInfo.getCheckField());
        map.put("dataSourceId", tbCustomizeDataManageInfo.getDataSourceId());
        map.put("eventType", tbCustomizeDataManageInfo.getEventType());
        map.put("marketCode", tbCustomizeDataManageInfo.getMarketCode());
        map.put("pageUrl", tbCustomizeDataManageInfo.getPageUrl());
        map.put("statisticsTimeStart", tbCustomizeDataManageInfo.getStatisticsTimeStart());
        map.put("statisticsTimeEnd", tbCustomizeDataManageInfo.getStatisticsTimeEnd());
        map.put("compareTimeStart", tbCustomizeDataManageInfo.getCompareTimeStart());
        map.put("compareTimeEnd", tbCustomizeDataManageInfo.getCompareTimeEnd());

        // 装配数据内容对象
        JSONObject data_content = new JSONObject();

        // 是否有对比时间
        boolean has_compare_time =
                tbCustomizeDataManageInfo.getCompareTimeStart() != null
                        && tbCustomizeDataManageInfo.getCompareTimeEnd() != null;
        data_content.put("has_compare_time", has_compare_time ? "1" : "0");

        // 需要展示的表头
        String checkField = tbCustomizeDataManageInfo.getCheckField();
        if (!StringUtils.isEmpty(checkField)) {
            List<String> headers = Arrays.asList(checkField.split(","));
            data_content.put("displayed_header", headers);
        }

        // 实际统计数据列表
        String statisticsContent = tbCustomizeDataManageInfo.getStatistics_content();

        JSONArray jsonArray = new JSONArray();

        try {
            jsonArray = JSONArray.parseArray(statisticsContent);
            if (null != jsonArray && jsonArray.size() != 0) {
                jsonArray = formatDataList(jsonArray, has_compare_time); // 格式化
            }
        } catch (Exception e) {
            log.error("数据列表转换异常", e);
        }

        data_content.put("statistics_data_list", jsonArray);

        map.put("data", data_content);
        return map;
    }

    /**
     * 查询自定义规则管理列表
     *
     * @param tbCustomizeDataManageInfo 自定义规则管理查询入参
     * @return 自定义规则管理集合
     */
    @Override
    public Map<String, Object> selectTbCustomizeDataManageInfoList(TbCustomizeDataManageInfo tbCustomizeDataManageInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "查询成功");
        PageHelper.startPage(tbCustomizeDataManageInfo.getPageNum(), tbCustomizeDataManageInfo.getPageSize());
        List<TbCustomizeDataManageInfo> tbCustomizeDataManageInfos =
                tbCustomizeDataManageInfoMapper.selectTbCustomizeDataManageInfoList(tbCustomizeDataManageInfo);
        PageInfo<TbCustomizeDataManageInfo> list1 = new PageInfo<>(tbCustomizeDataManageInfos);
        List<TbCustomizeDataManageInfo> list2 = list1.getList();
        map.put("data", list2);
        map.put("total", list1.getTotal());
        map.put("pageSize", list1.getPageSize());
        map.put("pageNum", list1.getPageNum());
        return map;
    }

    /**
     * 新增自定义规则管理
     *
     * @param tbCustomizeDataManageInfo 自定义规则管理新增信息
     */
    @Override
    public Map<String, Object> insertTbCustomizeDataManageInfo(TbCustomizeDataManageInfoInput tbCustomizeDataManageInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "插入成功");

        // 1.校验
        String ruleName = tbCustomizeDataManageInfo.getRuleName();
        String ruleDesc = tbCustomizeDataManageInfo.getRuleDesc();
        if (ruleName.length() > 50) {
            map.put("code", 1001);
            map.put("msg", "规则名称长度不能超过50");
            return map;
        }
        if (ruleDesc.length() > 500) {
            map.put("code", 1002);
            map.put("msg", "规则描述长度不能超过500");
            return map;
        }

        // 2.设置其他属性
        Date date = new Date();
        TbCustomizeDataManageInfo bean = new TbCustomizeDataManageInfo();
        BeanUtils.copyProperties(tbCustomizeDataManageInfo, bean);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        bean.setCreate_by(user.getNickName()); // 创建人姓名
        bean.setUpdate_by(user.getNickName()); // 修改人姓名
        bean.setCreator(user.getNickName());   // 创建人姓名
        bean.setCreateTime(date);
        bean.setUpdateTime(date);
        bean.setStatus("0"); // 规则数据状态，默认0

        int row = tbCustomizeDataManageInfoMapper.insertTbCustomizeDataManageInfo(bean);
        if (row == 1) {
            return map;
        } else {
            map.put("code", 1003);
            map.put("msg", "插入异常");
            return map;
        }
    }

    /**
     * 修改自定义规则管理
     *
     * @param tbCustomizeDataManageInfo 自定义规则管理修改入参
     * @return 结果
     */
    @Override
    public Map<String, Object> updateTbCustomizeDataManageInfo(TbCustomizeDataManageInfo tbCustomizeDataManageInfo) {
        Map<String, Object> map = new HashMap<String, Object>();

        tbCustomizeDataManageInfoMapper.updateTbCustomizeDataManageInfo(tbCustomizeDataManageInfo);
        return map;
    }

    /**
     * 删除自定义规则管理信息
     *
     * @param id 自定义规则管理主键
     * @return 结果
     */
    @Override
    public Map<String, Object> deleteTbCustomizeDataManageInfoById(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "删除成功");
        int row = tbCustomizeDataManageInfoMapper.deleteTbCustomizeDataManageInfoById(id);
        if (row == 1) {
            return map;
        } else {
            map.put("code", 1001);
            map.put("msg", "删除异常");
            return map;
        }
    }

    @Override
    public Map<String, Object> startTask(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "启动成功");

        TbCustomizeDataManageInfo infoById =
                tbCustomizeDataManageInfoMapper.selectTbCustomizeDataManageInfoById(id);

        if ("1".equals(infoById.getStatus())) {
            map.put("code", 1001);
            map.put("msg", "任务已经是运行的状态");
            return map;
        }
        if ("2".equals(infoById.getStatus())) {
            map.put("code", 1002);
            map.put("msg", "任务已完成");
            return map;
        }
        if ("3".equals(infoById.getStatus())) {
            map.put("code", 1003);
            map.put("msg", "任务已停止");
            return map;
        }
        if ("4".equals(infoById.getStatus())) {
            map.put("code", 1004);
            map.put("msg", "任务异常");
            return map;
        }
        if (!"0".equals(infoById.getStatus())) {
            map.put("code", 1005);
            map.put("msg", "任务状态异常");
            return map;
        }

        boolean hasCompareTime =
                infoById.getCompareTimeStart() != null && infoById.getCompareTimeEnd() != null;

        CustomizeRuleQueryVo vo = new CustomizeRuleQueryVo();
        BeanUtils.copyProperties(infoById, vo);
        Set<String> monthYear = extractMonthSet(vo);
        vo.setId(id);
        log.info("查询年月日 = {}", monthYear);
        vo.setAll_days(monthYear);

        String taskId = UUID.randomUUID().toString();

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            SqlSession sqlSession = null;
            try {
                taskThread.put(taskId, Thread.currentThread());

                sqlSession = clickHouseSqlSessionFactory.openSession();
                queryContexts.put(taskId, sqlSession);

                TbEasydataUserTrackinfoCompleteAllMapper mapper =
                        sqlSession.getMapper(TbEasydataUserTrackinfoCompleteAllMapper.class);

                log.info("Task executed by: " + Thread.currentThread().getName());

                Date start = new Date();
                TbCustomizeDataManageInfo info = new TbCustomizeDataManageInfo();
                info.setId(id);
                info.setTaskId(taskId);
                info.setQuery_start_time(start);
                info.setUpdateTime(start);
                info.setStatus("1");
                tbCustomizeDataManageInfoMapper.updateTbCustomizeDataManageInfo(info);

                List<Map<String, Object>> data = null;
                String task_status = "2";

                if (hasCompareTime) {
                    data = mapper.queryDynamicGroupWithCompareTime(vo);
                } else {
                    data = mapper.queryDynamicGroupWithoutCompareTime(vo);
                }

                String status =
                        tbCustomizeDataManageInfoMapper.selectTbCustomizeDataManageInfoById(id)
                                .getStatus();

                if ("1".equals(status)) {
                    Date end = new Date();
                    info.setQuery_end_time(new Date());
                    Long consumeTime = end.getTime() - start.getTime();
                    info.setConsume_time(consumeTime + "");
                    info.setStatistics_content(objectMapper.writeValueAsString(data));
                    info.setStatus(task_status);
                    info.setUpdateTime(end);
                    tbCustomizeDataManageInfoMapper.updateTbCustomizeDataManageInfo(info);
                }

            } catch (Exception e) {
                log.error("任务异常", e);
                String errorMsg = e.getMessage();

                if (e instanceof InterruptedException) {
                    TbCustomizeDataManageInfo info = new TbCustomizeDataManageInfo();
                    Date end = new Date();
                    info.setId(id);
                    info.setStatus("3");
                    info.setQuery_end_time(new Date());
                    info.setUpdateTime(end);
                    info.setError_msg(errorMsg);
                    tbCustomizeDataManageInfoMapper.updateTbCustomizeDataManageInfo(info);
                } else {
                    TbCustomizeDataManageInfo info = new TbCustomizeDataManageInfo();
                    Date end = new Date();
                    info.setId(id);
                    info.setStatus("4");
                    info.setQuery_end_time(new Date());
                    info.setUpdateTime(end);
                    info.setError_msg(errorMsg);
                    tbCustomizeDataManageInfoMapper.updateTbCustomizeDataManageInfo(info);
                }

            } finally {
                taskThread.remove(taskId);
                queryContexts.remove(taskId);
                if (sqlSession != null) {
                    sqlSession.close();
                }
            }
        }, threadPoolTaskExecutor);

        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            map.put("code", 200);
            map.put("msg", "启动成功");
            return map;
        } catch (Exception e) {
            map.put("code", 1006);
            map.put("msg", "启动失败");
            return map;
        }

        return map;
    }

    @Override
    public Map<String, Object> stopTask(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "停止成功");
        TbCustomizeDataManageInfo info = tbCustomizeDataManageInfoMapper.selectTbCustomizeDataManageInfoById(id);
        String taskId = info.getTaskId();
        String status = info.getStatus();
        if (!"1".equals(status)) {
            map.put("code", 1001);
            map.put("msg", "任务不是运行中的状态");
            return map;
        }
        if (StringUtils.isEmpty(taskId)) {
            map.put("code", 1002);
            map.put("msg", "任务不存在");
            return map;
        }

        // 获取查询上下文，尝试中断数据库查询
        Object context = queryContexts.get(taskId);
        if (context instanceof SqlSession) {
            ((SqlSession) context).close();
        }

        Thread thread = taskThread.get(taskId);
        if (thread != null) {
            thread.interrupt();
            log.info("已发送中断信号给线程: {}", thread.getName());
        }

        TbCustomizeDataManageInfo update = new TbCustomizeDataManageInfo();
        update.setId(id);
        update.setStatus("3");
        tbCustomizeDataManageInfoMapper.updateTbCustomizeDataManageInfo(update);
        return map;
    }

    @Override
    public Map<String, Object> queryChannelList() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", 200);
        map.put("msg", "查询成功");
        List<OptionBusinessVo> optionBusinessVos = tbCodingSchemeChannelNumberManageMapper.selectAll();
        map.put("data", optionBusinessVos);
        return map;
    }

    // 获取统计时间、对比时间的年月日字符串
    public Set<String> extractMonthSet(CustomizeRuleQueryVo vo) {
        Set<String> monthSet = new HashSet<>();

        Date compareTimeStart = vo.getCompareTimeStart();
        Date compareTimeEnd = vo.getCompareTimeEnd();
        Date statisticsTimeStart = vo.getStatisticsTimeStart();
        Date statisticsTimeEnd = vo.getStatisticsTimeEnd();

        // 处理统计时间
        if (statisticsTimeStart != null && statisticsTimeEnd != null) {
            addDayRange(monthSet, statisticsTimeStart, statisticsTimeEnd);
        }

        // 处理对比时间
        if (compareTimeStart != null && compareTimeEnd != null) {
            addDayRange(monthSet, compareTimeStart, compareTimeEnd);
        }

        return monthSet;
    }

    private void addDayRange(Set<String> daySet, Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        while (!start.isAfter(end)) {
            daySet.add(start.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            start = start.plusDays(1);
        }
    }

    public JSONArray formatDataList(JSONArray dataArray, boolean has_compare_time) {
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject item = dataArray.getJSONObject(i);

            // 处理 absolute_change（正数加+，负数不变）
            if (item.containsKey("absolute_change")) {
                try {
                    long value = item.getLongValue("absolute_change");
                    if (value > 0) {
                        item.put("absolute_change", "+" + value);
                    }
                } catch (Exception e) {
                    // 保持原值
                }
            }

            // 处理 change_rate（正数加+，非0加%）
            if (item.containsKey("change_rate")) {
                try {
                    double value = item.getDoubleValue("change_rate");
                    StringBuilder result = new StringBuilder();
                    if (value > 0) {
                        result.append("+");
                    }
                    result.append(value);
                    if (value != 0) {
                        result.append("%");
                    }
                    item.put("change_rate", result.toString());
                } catch (Exception e) {
                    // 保持原值
                }
            }

            // 处理 statistics_occupy（非0加%）
            if (item.containsKey("statistics_occupy")) {
                try {
                    double value = item.getDoubleValue("statistics_occupy");
                    if (value != 0) {
                        item.put("statistics_occupy", value + "%");
                    }
                } catch (Exception e) {
                    // 保持原值
                }
            }

            // 处理 compare_occupy（非0加%）
            if (item.containsKey("compare_occupy")) {
                try {
                    double value = item.getDoubleValue("compare_occupy");
                    if (value != 0) {
                        item.put("compare_occupy", value + "%");
                    }
                } catch (Exception e) {
                    // 保持原值
                }
            }
        }
        return dataArray;
    }
}

