package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysCollect;
import com.clothrent.entity.SysGoods;
import com.clothrent.entity.SysUser;
import com.clothrent.service.SysCollectService;
import com.clothrent.service.SysFileService;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品收藏表 前端控制器
 * </p>
 *
 * @author liuqiming
 * @since 2024-03-01
 */
@Controller
@RequestMapping({"/collect","home/collect"})
public class SysCollectController {

    private static  final Logger logger= LoggerFactory.getLogger(SysCollectController.class);

    @Autowired
    SysCollectService collectService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysGoodsService goodsService;

    @Autowired
    SysFileService fileService;

    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(SysCollect collect){
        Long userId = collect.getUserId();
        Long goodsId = collect.getGoodsId();
        QueryWrapper<SysCollect> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("goods_id",goodsId);
        List<SysCollect> collectList = collectService.list(queryWrapper);
        if(collectList.size()>0){
            collectService.remove(queryWrapper);
            return ResultUtil.success("取消成功!");
        }else{
            SysUser sysUser = userService.getById(userId);
            collect.setUserName(sysUser.getName());
            SysGoods sysGoods = goodsService.getById(goodsId);
            collect.setGoodsName(sysGoods.getName());
            collect.setGoodsImage(sysGoods.getGoodsPic1());
            collectService.save(collect);
            return ResultUtil.success("收藏成功!");

        }

    }


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/homelistPage")
    public String homelistPage() {
        return "home/collect/list";
    }
    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String listPage() {
        return "admin/collect/list";
    }

    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysCollect queryParam , HttpSession session) {
        QueryWrapper<SysCollect> queryWrapper=new QueryWrapper<>();
        queryWrapper.allEq(ToolsUtils.convertObjToMap(queryParam,false),false);
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysCollect> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysCollect> listPage = collectService.page(indexPage, queryWrapper);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        collectService.removeByIds(idList);
        return ResultUtil.success();
    }


}
