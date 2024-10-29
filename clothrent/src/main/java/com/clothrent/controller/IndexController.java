package com.clothrent.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.entity.SysGoods;
import com.clothrent.entity.SysUser;
import com.clothrent.service.SysCategoryService;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysOrderItemService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class IndexController {

    private static  final Logger logger=LoggerFactory.getLogger(IndexController.class);

    @Autowired
    SysGoodsService goodsService;

    @Autowired
    SysCategoryService categoryService;


    @Autowired
    SysOrderItemService orderItemService;

    @RequestMapping("/error/{status}")
    public String errorPage(@PathVariable Integer status,String msg,Error error,Exception exception,Model model){
        if(!StringUtils.isEmpty(msg)){
            model.addAttribute("errorMsg",msg);
        }
        if(error!=null){
            model.addAttribute("errorMsg",error.getMessage());
        }
        if(exception!=null){
            model.addAttribute("errorMsg",exception.getMessage());
        }
        switch (status){
            case 401:
            case 400:
            case 404:return "error/404";
            case 500:return "error/500";
            default:return "error/default";
        }
    }

    //联系我们页面
    @RequestMapping("/home/contact")
    public String contact(Model model){
        return "home/contact/contact-us";
    }


    @RequestMapping({"/index","","/home/index"})
    public String index(Model model, HttpSession session) {
        // 获取商品
        QueryWrapper<SysGoods> queryWrapperGoods=new QueryWrapper<>();
        queryWrapperGoods.orderBy(true,false, "sale_number");
        IPage<SysGoods> indexPageGoods = new Page<>(1,12);
        IPage<SysGoods> listPagegoods = goodsService.page(indexPageGoods, queryWrapperGoods);
        model.addAttribute("goodsList",listPagegoods.getRecords());

        queryWrapperGoods=new QueryWrapper<>();
        queryWrapperGoods.orderBy(true,false, "id");
        IPage<SysGoods> indexPageGoodsNew = new Page<>(1,12);
        IPage<SysGoods> listPagegoodsNew = goodsService.page(indexPageGoodsNew, queryWrapperGoods);
        model.addAttribute("goodsListNew",listPagegoodsNew.getRecords());

        List<SysGoods> recommendList=getRandomSix(goodsService);
        model.addAttribute("recommendList",recommendList);

        return "home/index";
    }

    // 随机获取六个
    private List<SysGoods> getRandomNum(SysGoodsService goodsService,int number){
        List<SysGoods> randomList=new ArrayList<>();

        List<SysGoods> goodsList = goodsService.list();
        for(int i=0;i<number;i++){
            int anInt = new Random().nextInt(goodsList.size());
            logger.debug("当前随机数 ： {}",anInt);
            randomList.add(goodsList.get(anInt));
        }
        return randomList;
    }

    // 随机获取六个
    private List<SysGoods> getRandomSix(SysGoodsService goodsService){
        List<SysGoods> randomList=new ArrayList<>();

        List<SysGoods> goodsList = goodsService.list();
        for(int i=0;i<6;i++){
            int anInt = new Random().nextInt(goodsList.size());
            logger.debug("当前随机数 ： {}",anInt);
            randomList.add(goodsList.get(anInt));
        }
        return randomList;
    }

    /**
     * 跳到前台登录
     * @param model
     * @param session
     * @return
     */
    @RequestMapping({"/home/login","login"})
    public String homeLogin(Model model, HttpSession session) {
        removeLogin(session);
        return "home/login";
    }

    /**
     * 跳到前台注册
     * @param model
     * @param session
     * @return
     */
    @RequestMapping({"/home/register"})
    public String homeRegister(Model model, HttpSession session) {
        return "home/register";
    }

    //后台主页
    @RequestMapping("/admin/index")
    public String adminIndex(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user==null) {
            return "/home/login";
        }
        SysUser loginUser= (SysUser) user;
        // 获取当前用户的未读消息
        Integer type = loginUser.getType();
        if(type==2){ // 普通用户不可进入后台主页
            return "/home/index";
        }
        model.addAttribute("UnReadMessage",0);


        return "admin/index";
    }

    //跳转到后台登录页面
    @RequestMapping({"/admin/login"})
    public String adminLogin(HttpSession session,Model model) {
        removeLogin(session);
        return "home/login";
    }

    @ApiOperation("退出登录")
    @RequestMapping( value = {"/logout","/home/logout"},method = RequestMethod.GET)
    public String logout(HttpSession session,Model model){
        removeLogin(session);
        return "home/login";
    }
    @ApiOperation("退出登录")
    @RequestMapping( value = {"/admin/logout"},method = RequestMethod.GET)
    public String adminLogout(HttpSession session,Model model){
        removeLogin(session);
        return "home/login";
    }

    private void removeLogin(HttpSession session){
        session.removeAttribute("user");
        session.removeAttribute("type");
        session.removeAttribute("admin");
    }

}
