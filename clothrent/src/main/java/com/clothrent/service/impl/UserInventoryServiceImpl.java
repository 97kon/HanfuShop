package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.UserInventory;
import com.clothrent.mapper.UserInventoryMapper;
import com.clothrent.service.UserInventoryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存变动记录表 服务实现类
 * </p>
 *
 */
@Service
public class UserInventoryServiceImpl extends ServiceImpl<UserInventoryMapper, UserInventory> implements UserInventoryService {

}
