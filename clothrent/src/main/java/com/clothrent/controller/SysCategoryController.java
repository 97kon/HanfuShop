package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysCategory;
import com.clothrent.entity.SysGoods;
import com.clothrent.service.SysCategoryService;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 分类表 前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping({"/category","home/category"})
public class SysCategoryController {

    private static  final Logger logger= LoggerFactory.getLogger(SysCategoryController.class);

    @Autowired
    private SysCategoryService categoryService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysGoodsService goodsService;

    @ModelAttribute
    private void addModelAttr(Model model){
        QueryWrapper<SysCategory> queryWrapper1=new QueryWrapper<>();
        queryWrapper1.eq("parent_id",0);
        model.addAttribute("parentList",categoryService.list(queryWrapper1));
        // 一级分类
        QueryWrapper<SysCategory> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id",0);
        List<SysCategory> firstCategoryList = categoryService.list(queryWrapper);
        model.addAttribute("firstCategoryList",firstCategoryList);
        // 二级分类
        QueryWrapper<SysCategory> childqueryWrapper=new QueryWrapper<>();
        childqueryWrapper.ne("parent_id",0);
        model.addAttribute("secondCategoryList",categoryService.list(childqueryWrapper));
    }

    // 获取二级分类菜单
    @RequestMapping("seconList")
    @ResponseBody
    public ResponseBean seconList(Model model){
        return ResultUtil.successData(model.getAttribute("secondCategoryList"));
    }

    // 商品列表页面--左侧分类菜单
    @RequestMapping("menuList")
    @ResponseBody
    public ResponseBean menuList(Model model){
        List<SysCategory> parentList = (List<SysCategory>) model.getAttribute("parentList");
        for(SysCategory category:parentList){
            QueryWrapper<SysCategory> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("parent_id",category.getId());
            List<SysCategory> childCategoryList = categoryService.list(queryWrapper);
            for(SysCategory currObj:childCategoryList){//计算每一类的商品个数
                QueryWrapper<SysGoods> countwrapper=new QueryWrapper<>();
                countwrapper.eq("category_id",currObj.getId());
                currObj.setField0(String.valueOf(goodsService.count(countwrapper)));//记录该分类的商品个数
            }
            category.setChildList(childCategoryList);
        }
        logger.debug("最后封装的商品分类数据为：{}",parentList);
        return ResultUtil.successData(parentList);
    }

    /**
     * 跳转到增加页面
     * @return
     */
    @RequestMapping("addPage")
    public String addPage(){
        return "admin/category/add";
    }

    // 发布 添加
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(SysCategory category, HttpSession session){
        logger.debug("addSysCategory:"+category);
        if(category==null){
            return  ResultUtil.error();
        }
        SysCategory parentObj = categoryService.getById(category.getParentId());
        if(parentObj!=null){
            category.setParentName(parentObj.getName());
        }else{
            category.setParentName("顶级分类");
        }

        categoryService.save(category);
        logger.debug("分类添加结果："+category);

        return ResultUtil.successData(category);
    }

    /**
     * 跳转到分类修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/editPage/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getById(id));
        return "admin/category/edit";
    }


    /**
     * 分类修改
     * @param category
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public ResponseBean updateById(SysCategory category, HttpSession session) {
        if (category == null) {
            return  ResultUtil.error();
        }
        SysCategory parentObj = categoryService.getById(category.getParentId());
        if(parentObj!=null){
            category.setParentName(parentObj.getName());
        }else{
            category.setParentName("顶级分类");
        }
        category.setUpdateTime(LocalDateTime.now());
        categoryService.updateById(category);
        return ResultUtil.successData(category);
    }


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String categoryListPage() {
        return "admin/category/list";
    }

    /**
     * 跳转前台分类列表页面
     * @return
     */
    @RequestMapping("/homeListPage")
    public String homeListPage() {
        return "home/category/category_list";
    }

    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询分类列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysCategory queryParam , HttpSession session, String beginDate, String endDate) {
        logger.debug("查询分类列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<SysCategory> queryWrapper=new QueryWrapper<>();
        queryWrapper.allEq(ToolsUtils.convertObjToMap(queryParam,false),false);
        queryWrapper.ne("parent_id",-1);

        queryWrapper.or().like(!StringUtils.isEmpty(queryParam.getName()),"name",queryParam.getName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysCategory> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysCategory> listPage = categoryService.page(indexPage, queryWrapper);
        logger.debug("获取的分类列表："+listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 分类删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        categoryService.removeByIds(idList);
        return ResultUtil.success();
    }


}
