package com.clothrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clothrent.entity.SysOrder;
import org.aspectj.apache.bcel.classfile.Code;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 */
public interface SysOrderMapper extends BaseMapper<SysOrder> {
//    List<SysOrder> getByCode(String code);

    SysOrder getByCode(String code);

}
