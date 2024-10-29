package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.SysOrderItem;
import com.clothrent.mapper.SysOrderItemMapper;
import com.clothrent.service.SysOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 */
@Service
public class SysOrderItemServiceImpl extends ServiceImpl<SysOrderItemMapper, SysOrderItem> implements SysOrderItemService {

    @Autowired
    SysOrderItemMapper orderItemMapper;
    @Override
    public List<SysOrderItem> getByOrderId(Long orderId) {
        return orderItemMapper.getByOrderId( orderId);
    }
}
