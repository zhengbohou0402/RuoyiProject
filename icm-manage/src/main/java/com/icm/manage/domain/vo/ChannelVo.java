package com.icm.manage.domain.vo;

import com.icm.manage.domain.Channel;
import com.icm.manage.domain.Sku;
import lombok.Data;

@Data
public class ChannelVo extends Channel {

    // 商品对象
    private Sku sku;
}
