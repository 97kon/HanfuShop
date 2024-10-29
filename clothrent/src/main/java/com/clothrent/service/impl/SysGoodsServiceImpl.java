package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.entity.SysGoods;
import com.clothrent.mapper.SysGoodsMapper;
import com.clothrent.service.SysGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 */
@Service
public class SysGoodsServiceImpl extends ServiceImpl<SysGoodsMapper, SysGoods> implements SysGoodsService {

    @Autowired
    SysGoodsMapper goodsMapper;

    @Override
    public List<String> getOriginGoodsIdList() {

        return goodsMapper.getOriginGoodsIdList();
    }

    @Override
    public List<HashMap> commentNumTopN() {
        return goodsMapper.commentNumTopN();
    }



    @Override
    public List<HashMap> feedbackRateTopN() {
        return goodsMapper.feedbackRateTopN();
    }

    @Override
    public List<HashMap> priceTopN() {
        return goodsMapper.priceTopN();
    }

    @Override
    public List<HashMap> avgPriceGroupCategory() {
        return goodsMapper.avgPriceGroupCategory();
    }

    @Override
    public List<HashMap> avgRateGroupCategory() {
        return goodsMapper.avgRateGroupCategory();
    }

    @Override
    public List<HashMap> avgCommentGroupCategory() {
        return goodsMapper.avgCommentGroupCategory();
    }

    @Override
    public List<HashMap> countNumGroupCategory() {
        return goodsMapper.countNumGroupCategory();
    }

    @Override
    public List<HashMap> countCommentNumGroupCategory() {
        return goodsMapper.countCommentNumGroupCategory();
    }

    @Override
    public SysGoods getOneDiscount() {
        return goodsMapper.getOneDiscount();
    }
}
