package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysCart;
import com.clothrent.entity.SysGoods;
import com.clothrent.entity.SysUser;
import com.clothrent.service.SysCartService;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping({"/cart","/home/cart"})
public class SysCartController {

    private static  final Logger logger= LoggerFactory.getLogger(SysCartController.class);

    @Autowired
    SysCartService cartService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysGoodsService goodsService;


    // 购物车更新
    @RequestMapping("edit")
    @ResponseBody
    public ResponseBean editCart(SysCart cart, HttpSession session){
        logger.debug("editCart:"+cart);
        if(cart==null){
            return  ResultUtil.error();
        }
        if(cart.getId()==null){
            return ResultUtil.error("主键为空！");
        }
        cartService.updateById(cart);
        logger.debug("购物车更新结果："+cart);

        // 这里采用提交订单付款减库存方式，添加购物车不减少商品库存
        return ResultUtil.successData(cart);
    }


    // 批量加入购物车
    @RequestMapping("addBatchCart")
    @ResponseBody
    public ResponseBean addBatchCart(Long userId,String goodsIdList){
        if(userId==null|| StringUtils.isEmpty(goodsIdList)){
            return ResultUtil.error("请选择商品！");
        }
        String[] split = goodsIdList.split(",");
        for(String goodsId:split){
            SysCart sysCart=new SysCart();
            sysCart.setGoodsId(Long.parseLong(goodsId));
            sysCart.setNumber(1);
            sysCart.setUserId(userId);
            add(sysCart,null);
        }
        return ResultUtil.success();

    }


    // 购物车添加商品
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(SysCart cart, HttpSession session){
        logger.debug("addSysCart:"+cart);
        if(cart==null){
            return  ResultUtil.error();
        }

        Long goodsId = cart.getGoodsId();
        Long userId = cart.getUserId();
        // 判断当前用户是否已经添加该商品到购物车
        QueryWrapper<SysCart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("goods_id",goodsId);
        queryWrapper.eq("cloth_size",cart.getClothSize());
        queryWrapper.eq("user_id",userId);
        SysCart sysCart = cartService.getOne(queryWrapper);
        if(sysCart!=null){
            sysCart.setNumber(sysCart.getNumber()+cart.getNumber());
            cartService.updateById(sysCart);
            return ResultUtil.successData(sysCart);
        }
        // 否则认定为用户第一次添加购物车
        SysGoods goods = goodsService.getById(goodsId);
        cart.setGoodsName(goods.getName());
        cart.setField1(goods.getGoodsPic1());
        cart.setCategoryId(goods.getCategoryId());
        cart.setCategoryName(goods.getCategoryName());
        cart.setPrice(goods.getDiscount()==null?goods.getPrice():goods.getDiscount());

        SysUser sysUser = userService.getById(userId);
        cart.setUserName(sysUser.getName());
        cartService.save(cart);
        logger.debug("购物车添加结果：{}",cart);

        // 这里采用提交订单付款减库存方式，添加购物车不减少商品库存
        return ResultUtil.successData(cart);
    }



    /**
     * 跳转前台购物车详情页面--即我的购物车页面
     * @return
     */
    @RequestMapping("/detail")
    public String homeCartDetailPage(Long userId,Model model,HttpSession session) {
        if(ToolsUtils.getLoginUser(session)==null){
            return "home/login";
        }
        return "home/cart/detail";
    }



    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询购物车列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysCart queryParam , HttpSession session, String beginDate, String endDate) {
        logger.debug("查询购物车列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<SysCart> queryWrapper=new QueryWrapper<>();
        queryWrapper.allEq(ToolsUtils.convertObjToMap(queryParam,false),false);

        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysCart> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysCart> listPage = cartService.page(indexPage, queryWrapper);
        logger.debug("获取的购物车列表："+listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 购物车删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        cartService.removeByIds(idList);
        return ResultUtil.success();
    }




}
