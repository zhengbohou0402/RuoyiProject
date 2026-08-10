package com.dkd.manage.mapper;

import com.dkd.common.annotation.DataSource;
import com.dkd.common.enums.DataSourceType;
import com.dkd.manage.domain.dto.UserBehaviorTrackQueryDTO;
import com.dkd.manage.domain.vo.TbUserBehaviorTrackVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户行为轨迹 Mapper
 */
public interface UserBehaviorTrackMapper {

    /**
     * 分页查询用户行为轨迹列表
     */
    @DataSource(DataSourceType.CLICKHOUSE)
    List<TbUserBehaviorTrackVo> selectUserBehaviorTrackList(UserBehaviorTrackQueryDTO query);

    /**
     * 查询总数（用于分页）
     */
    @DataSource(DataSourceType.CLICKHOUSE)
    Long countUserBehaviorTrack(UserBehaviorTrackQueryDTO query);

    /**
     * 【模拟长连接-数据生成器用】插入一条轨迹数据到 ClickHouse
     */
    @DataSource(DataSourceType.CLICKHOUSE)
    int insertTrackData(@Param("daytime") Date daytime,
                         @Param("statisDate") Date statisDate,
                         @Param("mobile") String mobile,
                         @Param("wtEt") String wtEt,
                         @Param("wtTi") String wtTi,
                         @Param("wtEs") String wtEs,
                         @Param("wtEnvName") String wtEnvName,
                         @Param("wtEvent") String wtEvent);

    /**
     * 【模拟长连接-增量轮询用】查询某手机号在 afterTime 之后新增的轨迹数据
     */
    @DataSource(DataSourceType.CLICKHOUSE)
    List<TbUserBehaviorTrackVo> selectIncrementalTrack(@Param("phone") String phone,
                                                        @Param("afterTime") Date afterTime);

    /**
     * 【模拟长连接-增量轮询用】查询增量数据里最大的 daytime，作为下次轮询的基准时间
     * （用数据本身的最大时间而非系统时间，防止入库延迟导致漏查，与 UserBehaviorLog 增量查询同一思路）
     */
    @DataSource(DataSourceType.CLICKHOUSE)
    Date selectMaxDaytime(@Param("phone") String phone, @Param("afterTime") Date afterTime);
}


