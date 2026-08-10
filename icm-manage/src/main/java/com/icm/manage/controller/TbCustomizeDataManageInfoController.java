package com.icm.manage.controller;

import com.icm.common.core.controller.BaseController;
import com.icm.manage.domain.TbCustomizeDataManageInfo;
import com.icm.manage.domain.vo.TbCustomizeDataManageInfoInput;
import com.icm.manage.service.ITbCustomizeDataManageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 自定义数据管理信息Controller
 */
@RestController
@RequestMapping("/customizeDataManage")
public class TbCustomizeDataManageInfoController extends BaseController {

    @Autowired
    private ITbCustomizeDataManageInfoService tbCustomizeDataManageInfoService;

    /**
     * 查询自定义数据管理信息列表
     */
    @RequestMapping("/list")
    public Map<String, Object> list(@RequestBody TbCustomizeDataManageInfo tbCustomizeDataManageInfo) {
        return tbCustomizeDataManageInfoService
                .selectTbCustomizeDataManageInfoList(tbCustomizeDataManageInfo);
    }

    /**
     * 获取自定义数据管理信息详细信息
     */
    @RequestMapping(value = "/detail/{id}")
    public Map<String, Object> getInfo(@PathVariable("id") Long id) {
        return tbCustomizeDataManageInfoService
                .selectTbCustomizeDataManageInfoById(id);
    }

    /**
     * 新增自定义数据管理信息
     */
    @RequestMapping("/add")
    public Map<String, Object> add(
            @RequestBody @Validated TbCustomizeDataManageInfoInput tbCustomizeDataManageInfo) {
        return tbCustomizeDataManageInfoService
                .insertTbCustomizeDataManageInfo(tbCustomizeDataManageInfo);
    }

    /**
     * 删除自定义数据管理信息
     */
    @RequestMapping("/delete/{id}")
    public Map<String, Object> remove(@PathVariable Long id) {
        return tbCustomizeDataManageInfoService
                .deleteTbCustomizeDataManageInfoById(id);
    }

    /**
     * 启动查询任务
     *
     * @param id
     * @return
     */
    @RequestMapping("/start/{id}")
    public Map<String, Object> startTask(@PathVariable Long id) {
        return tbCustomizeDataManageInfoService.startTask(id);
    }

    /**
     * 停止查询任务
     *
     * @param id
     * @return
     */
    @RequestMapping("/stop/{id}")
    public Map<String, Object> stopTask(@PathVariable Long id) {
        return tbCustomizeDataManageInfoService.stopTask(id);
    }

    /**
     * 查询所有渠道号
     */
    @RequestMapping("/queryChannelList")
    public Map<String, Object> queryChannelList() {
        return tbCustomizeDataManageInfoService.queryChannelList();
    }
}

