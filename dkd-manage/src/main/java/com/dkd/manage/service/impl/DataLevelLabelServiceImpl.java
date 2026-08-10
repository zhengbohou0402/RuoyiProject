package com.dkd.manage.service.impl;

import com.dkd.common.exception.ServiceException;
import com.dkd.common.utils.DateUtils;
import com.dkd.common.utils.StringUtils;
import com.dkd.manage.domain.DataLevelLabel;
import com.dkd.manage.mapper.DataLevelLabelMapper;
import com.dkd.manage.service.IDataLevelLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dkd.common.utils.SecurityUtils.getLoginUser;

/**
 * 数据等级标注Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-16
 */
@Service
public class DataLevelLabelServiceImpl implements IDataLevelLabelService {
    @Autowired
    private DataLevelLabelMapper dataLevelLabelMapper;

    /**
     * 查询数据等级标注列表
     *
     * @param dataLevelLabel 数据等级标注
     * @return 数据等级标注
     */
    @Override
    public List<DataLevelLabel> selectDataLevelLabelList(DataLevelLabel dataLevelLabel) {
        return dataLevelLabelMapper.selectDataLevelLabelList(dataLevelLabel);
    }

    /**
     * 新增数据等级标注
     *
     * @param dataLevelLabel 数据等级标注
     * @return 结果
     */
    @Override
    public int insertDataLevelLabel(DataLevelLabel dataLevelLabel) {
        // 1. 【准备工作】获取当前登录人和当前时间 (只获取一次，性能最高)
        // 注意：如果在 Service 层用不了 getLoginUser()，请换成 SecurityUtils.getLoginUser()
        String currentUser = getLoginUser().getUser().getNickName();
        Date now = DateUtils.getNowDate();

        // 2. 准备大集合
        List<DataLevelLabel> batchList = new ArrayList<>();

        // 获取前端传来的数组
        List<String> cIds = dataLevelLabel.getChannelIds();
        List<String> eCodes = dataLevelLabel.getEventCodes();

        // 3. 双重循环组装数据
        if (cIds != null && !cIds.isEmpty() && eCodes != null && !eCodes.isEmpty()) {
            for (String cid : cIds) {
                for (String code : eCodes) {
                    DataLevelLabel item = new DataLevelLabel();

                    // --- 核心业务数据 ---
                    item.setChannelId(cid);
                    item.setEventCode(code);
                    item.setDataLevel(dataLevelLabel.getDataLevel());

                    // --- 审计字段 (使用你刚才提供的代码) ---
                    item.setCreateBy(currentUser);
                    item.setCreateTime(now);

                    batchList.add(item);
                }
            }
        }

        // 4. 批量插入
        if (!batchList.isEmpty()) {
            return dataLevelLabelMapper.batchInsertDataLevelLabel(batchList);
        }
        return 0;
    }

    @Override
    public List<DataLevelLabel> checkBatchUnique(DataLevelLabel input) {
        List<String> cIds = input.getChannelIds();
        List<String> eCodes = input.getEventCodes();

        // 如果参数为空，说明没得查，直接返回空列表
        if (cIds == null || cIds.isEmpty() || eCodes == null || eCodes.isEmpty()) {
            return new ArrayList<>();
        }

        // 领导口径：是否“冲突/重复”只看唯一键(channel_id,event_code)是否已存在
        // 所以直接把数据库已存在的记录列表返回给 Controller 即可
        List<DataLevelLabel> dbList = dataLevelLabelMapper.selectExistingList(cIds, eCodes);
        return dbList == null ? new ArrayList<>() : dbList;
    }

    /**
     * 修改数据等级标注
     *
     * @param dataLevelLabel 数据等级标注
     * @return 结果
     */
    @Override
    public int updateDataLevelLabel(DataLevelLabel dataLevelLabel) {
        // 1. 先查旧数据
        DataLevelLabel oldData = dataLevelLabelMapper.selectDataLevelLabelById(dataLevelLabel.getId());

        // 2. 【核心】比对：如果新旧等级一样
        if (StringUtils.equals(oldData.getDataLevel(), dataLevelLabel.getDataLevel())) {
            // 抛出这个异常后：
            // 1. 程序立即停止，不会去连数据库。
            // 2. 前端界面正上方会弹出一个提示框，内容就是括号里的这句话。
            throw new ServiceException("数据等级未发生变化！");
        }
        dataLevelLabel.setUpdateBy(getLoginUser().getUser().getNickName());
        dataLevelLabel.setUpdateTime(DateUtils.getNowDate());
        return dataLevelLabelMapper.updateDataLevelLabel(dataLevelLabel);
    }


    /**
     * 删除数据等级标注信息
     *
     * @param id 数据等级标注主键
     * @return 结果
     */
    @Override
    public int deleteDataLevelLabelById(Long id) {
        return dataLevelLabelMapper.deleteDataLevelLabelById(id);
    }

    @Override
    public List<String> selectAllEventCodes() {
        return dataLevelLabelMapper.selectAllEventCodes();
    }

    @Override
    public List<String> selectAllChannelIds() {
        return dataLevelLabelMapper.selectAllChannelIds();
    }
}