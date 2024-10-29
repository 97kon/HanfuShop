package com.clothrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clothrent.entity.SysOrderItem;

import java.util.List;

/**
 * <p>
 * 订单明细表 Mapper 接口
 * </p>
 *
 */
public interface SysOrderItemMapper extends BaseMapper<SysOrderItem> {

    List<SysOrderItem> getByOrderId(Long orderId);
}
