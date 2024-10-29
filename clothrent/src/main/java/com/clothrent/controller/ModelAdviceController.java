package com.clothrent.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clothrent.common.ToolsUtils;
import com.clothrent.entity.SysCart;
import com.clothrent.entity.SysCategory;
import com.clothrent.entity.SysUser;
import com.clothrent.service.SysCartService;
import com.clothrent.service.SysCategoryService;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * controller前置处理
 */
@ControllerAdvice
public class ModelAdviceController {
    private static final Logger logger= LoggerFactory.getLogger(ModelAdviceController.class);

    @Autowired
    SysUserService userService;

    @Autowired
    SysCartService cartService;

    @Autowired
    SysCategoryService categoryService;

    @Autowired
    SysGoodsService goodsService;

    // 如果当前用户登录不为空，则尝试获取用户购物车数据
    @ModelAttribute
    public void addModelInfo(Model model, HttpSession session){
        SysUser loginUser = ToolsUtils.getLoginUser(session);
        //如果当前用户登录不为空，则尝试获取用户购物车数据
        if(loginUser!=null){
            Long id = loginUser.getId();
            // 更新session中最新用户数据 -begin
            SysUser currUser = userService.getById(id);
            session.setAttribute("user",currUser);
            session.setAttribute("type",currUser.getType());
            // 更新session中最新用户数据 -end

            //更新用户购物车数据
            QueryWrapper<SysCart>  queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("user_id",id);
            List<SysCart> cartList = cartService.list(queryWrapper);
            logger.debug("当前尝试获取用户的购物车数据为：{}",cartList);
            session.setAttribute("cartListTotal",0);
            if(cartList!=null&&!cartList.isEmpty()){
                BigDecimal bigDecimal = cartList.stream().map(e -> e.getPrice().multiply(new BigDecimal(e.getNumber()))).reduce(BigDecimal::add).get();
                session.setAttribute("cartListTotal",bigDecimal.floatValue());
            }
            session.setAttribute("cartList",cartList==null? Collections.emptyList():cartList);
        }
//        // 计算分类菜单
//        QueryWrapper<SysCategory> pqueryWrapper=new QueryWrapper<>();
//        pqueryWrapper.eq("parent_id",0);
//        List<SysCategory> parentList = categoryService.list(pqueryWrapper);
//        for(SysCategory category:parentList){
//            QueryWrapper<SysCategory> queryWrapper=new QueryWrapper<>();
//            queryWrapper.eq("parent_id",category.getId());
//            List<SysCategory> childCategoryList = categoryService.list(queryWrapper);
//            for(SysCategory currObj:childCategoryList){//计算每一类的商品个数
//                QueryWrapper<SysGoods> countwrapper=new QueryWrapper<>();
//                countwrapper.eq("category_id",currObj.getId());
//                currObj.setField0(String.valueOf(goodsService.count(countwrapper)));//记录该分类的商品个数
//            }
//            category.setChildList(childCategoryList);
//        }
//        model.addAttribute("categoryMenuList",parentList);
//        session.setAttribute("categoryMenuList",parentList);

        // 一级分类
        QueryWrapper<SysCategory> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id",0);
        List<SysCategory> firstCategoryList = categoryService.list(queryWrapper);
        model.addAttribute("firstCategoryList",firstCategoryList);
        session.setAttribute("firstCategoryList",firstCategoryList);
        // 二级分类
        QueryWrapper<SysCategory> childqueryWrapper=new QueryWrapper<>();
        childqueryWrapper.ne("parent_id",0);
        List<SysCategory> secondCategoryList = categoryService.list(childqueryWrapper);
        model.addAttribute("secondCategoryList", secondCategoryList);
        session.setAttribute("secondCategoryList",secondCategoryList);

    }

}
