package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.SysCart;
import com.clothrent.mapper.SysCartMapper;
import com.clothrent.service.SysCartService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 */
@Service
public class SysCartServiceImpl extends ServiceImpl<SysCartMapper, SysCart> implements SysCartService {

}
