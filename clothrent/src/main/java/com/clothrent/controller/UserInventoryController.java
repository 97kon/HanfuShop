package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysGoods;
import com.clothrent.entity.UserInventory;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysUserService;
import com.clothrent.service.UserInventoryService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 库存变动记录表 前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping({"/inventory","home/inventory"})
public class UserInventoryController {

    private static  final Logger logger= LoggerFactory.getLogger(UserInventoryController.class);

    @Autowired
    private UserInventoryService inventoryService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysGoodsService goodsService;

    @ModelAttribute
    private void addModelAttr(Model model){
        model.addAttribute("goodsList",goodsService.list());
    }


    // 跳到商品入库页面
    @RequestMapping("addPage")
    public String addPage(Integer goodsId,Model model){
        if(goodsId!=null){
            List<SysGoods> goodsList = (List<SysGoods>) model.getAttribute("goodsList");
            List<SysGoods> collect = goodsList.stream().filter(sysGoods -> sysGoods.getId().intValue() == goodsId).collect(Collectors.toList());
            logger.debug("当前采购的物品ID：{}，放入model的goodsList为：{}",goodsId,collect);
            model.addAttribute("goodsList",collect);
        }
        return "admin/inventory/add";
    }

    // 添加  商品入库
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(UserInventory inventory, HttpSession session){
        logger.debug("addUserInventory:"+inventory);
        if(inventory==null){
            return  ResultUtil.error();
        }
        SysGoods goods = goodsService.getById(inventory.getGoodsId());
        inventory.setGoodsName(goods.getName());
        int beforeStock = goods.getStock().intValue();
        inventory.setBeforeStock(beforeStock);
        inventory.setCurrentStock(beforeStock+inventory.getChangeStock());
        StringBuilder stringBuilder=new StringBuilder();
        inventory.setUserName(ToolsUtils.getLoginUserName(session));
        stringBuilder.append(inventory.getUserName()).append("采购入库了【").append(inventory.getChangeStock())
                .append("】个").append(goods.getName()).append(",总费用为：").append(inventory.getPrice())
                .append("，供应商是：").append(inventory.getSupplier());
        inventory.setUserId(ToolsUtils.getLoginUserId(session));
        inventory.setReason(stringBuilder.toString());
        inventoryService.save(inventory);
        logger.debug("库存记录添加结果："+inventory);

        // 修改商品库存
        goods.setStock(inventory.getCurrentStock().longValue());
        goodsService.updateById(goods);

        return ResultUtil.successData(inventory);
    }



    /**
     * 跳转入库记录列表页面
     * @return
     */
    @RequestMapping("/listInPage")
    public String inventoryListInPage() {
        return "admin/inventory/list_in";
    }

    /**
     * 跳转出库记录列表页面
     * @return
     */
    @RequestMapping("/listOutPage")
    public String inventoryListOutPage() {
        return "admin/inventory/list_out";
    }



    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询库存记录列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, UserInventory queryParam , HttpSession session) {
        logger.debug("查询库存记录列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<UserInventory> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(queryParam.getType()!=null,"type",queryParam.getType());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getGoodsName()),"goods_name",queryParam.getGoodsName());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getUserName()),"user_name",queryParam.getUserName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<UserInventory> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<UserInventory> listPage = inventoryService.page(indexPage, queryWrapper);
        logger.debug("获取的库存记录列表："+listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 库存记录删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        inventoryService.removeByIds(idList);
        return ResultUtil.success();
    }



}
