package com.clothrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clothrent.entity.SysGoods;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 */
public interface SysGoodsMapper extends BaseMapper<SysGoods> {

    // 获取一个折扣力度最大的商品 price-discount
    SysGoods getOneDiscount();

    List<String> getOriginGoodsIdList();

    List<HashMap> countNumGroupCategory();
    List<HashMap> countCommentNumGroupCategory();
    List<HashMap> commentNumTopN();
    List<HashMap> feedbackRateTopN();
    List<HashMap> priceTopN();
    List<HashMap> avgPriceGroupCategory();
    List<HashMap> avgRateGroupCategory();
    List<HashMap> avgCommentGroupCategory();
}
