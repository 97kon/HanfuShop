package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.SysCategory;
import com.clothrent.mapper.SysCategoryMapper;
import com.clothrent.service.SysCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 分类表 服务实现类
 * </p>
 *
 */
@Service
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements SysCategoryService {

    @Autowired
    SysCategoryMapper categoryMapper;

    @Override
    public SysCategory findByCode(String code) {
        return categoryMapper.findByCode(code);
    }
}
