package com.icm.manage.domain.vo;

import com.icm.manage.domain.Node;
import com.icm.manage.domain.Partner;
import com.icm.manage.domain.Region;
import lombok.Data;

@Data
public class NodeVo extends Node {

    // 设备数量
    private Integer vmCount;

    // 区域信息
    private Region region;

    // 合作商信息
    private Partner partner;
}
