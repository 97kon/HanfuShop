package com.clothrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clothrent.entity.SysDictValue;

import java.util.List;

/**
 * <p>
 * 字典值表 Mapper 接口
 * </p>
 *
 */
public interface SysDictValueMapper extends BaseMapper<SysDictValue> {

    List<SysDictValue> getValueByCode(String dictCode);
}
