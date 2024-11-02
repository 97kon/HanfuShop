package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.UserAccount;
import com.clothrent.mapper.UserAccountMapper;
import com.clothrent.service.UserAccountService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户表 服务实现类
 * </p>
 *
 * @author liuqiming
 * @since 2024-03-01
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}
