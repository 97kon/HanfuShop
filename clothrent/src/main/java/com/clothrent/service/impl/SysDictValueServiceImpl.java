package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.SysDictValue;
import com.clothrent.mapper.SysDictValueMapper;
import com.clothrent.service.SysDictValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典值表 服务实现类
 * </p>
 *
 */
@Service
public class SysDictValueServiceImpl extends ServiceImpl<SysDictValueMapper, SysDictValue> implements SysDictValueService {

    @Autowired
    SysDictValueMapper dictValueMapper;

    @Override
    public List<SysDictValue> getValueByCode(String dictCode) {
        return dictValueMapper.getValueByCode( dictCode);
    }
}
