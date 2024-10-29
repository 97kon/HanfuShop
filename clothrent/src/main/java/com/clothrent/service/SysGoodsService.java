package com.clothrent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clothrent.entity.SysGoods;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 */
public interface SysGoodsService extends IService<SysGoods> {

    SysGoods getOneDiscount();

    List<String> getOriginGoodsIdList();
    List<HashMap> commentNumTopN();
    List<HashMap> feedbackRateTopN();
    List<HashMap> priceTopN();
    List<HashMap> avgPriceGroupCategory();
    List<HashMap> avgRateGroupCategory();
    List<HashMap> avgCommentGroupCategory();
    List<HashMap> countNumGroupCategory();
    List<HashMap> countCommentNumGroupCategory();
}
