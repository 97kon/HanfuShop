package com.clothrent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clothrent.entity.SysDictValue;

import java.util.List;

/**
 * <p>
 * 字典值表 服务类
 * </p>
 *
 */
public interface SysDictValueService extends IService<SysDictValue> {

    List<SysDictValue> getValueByCode(String dictCode);

}
