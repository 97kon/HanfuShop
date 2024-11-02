package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.GoodsAppraise;
import com.clothrent.entity.SysOrder;
import com.clothrent.entity.SysOrderItem;
import com.clothrent.entity.SysUser;
import com.clothrent.service.GoodsAppraiseService;
import com.clothrent.service.SysOrderItemService;
import com.clothrent.service.SysOrderService;
import com.clothrent.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品评价表 前端控制器
 * </p>
 *
 * @author liuqiming
 * @since 2023-11-08
 */
@RestController
@RequestMapping({"/goodsAppraise","home/goodsAppraise"})
public class GoodsAppraiseController {

    private static  final Logger logger= LoggerFactory.getLogger(GoodsAppraiseController.class);

    @Autowired
    private GoodsAppraiseService goodsAppraiseService;

    @Autowired
    SysOrderItemService orderItemService;

    @Autowired
    SysOrderService orderService;

    @Autowired
    SysUserService userService;

    /**
     * 商品评价
     * @param content 评价内容
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(Long orderItemId, String image , String content){
        SysOrderItem orderItem = orderItemService.getById(orderItemId);
        String code = orderItem.getCode();
        QueryWrapper<SysOrder> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("code",code);
        SysOrder order = orderService.getOne(queryWrapper);
        Long userId = order.getUserId();
        SysUser user = userService.getById(userId);

        GoodsAppraise appraise=new GoodsAppraise();
        appraise.setContent(content);
        appraise.setGoodsImg(orderItem.getField1());
        appraise.setGoodsId(orderItem.getGoodsId());
        appraise.setGoodsName(orderItem.getGoodsName());
        appraise.setOrderItemId(orderItemId);
        appraise.setOrderId(order.getId());
        appraise.setUserId(user.getId());
        appraise.setUserName(user.getName());
        appraise.setUserImg(user.getField1());
        appraise.setImage(image);

        //保存到数据库
        goodsAppraiseService.save(appraise);

        //更新当前订单明细状态为8 已评价
        orderItem.setState(8);
        orderItemService.updateById(orderItem);

        //判断当前订单下所有订单明细是否均已评价，如果是，则更新订单状态为已评价
        QueryWrapper<SysOrderItem> itemQueryWrapper=new QueryWrapper<>();
        itemQueryWrapper.eq("code",code).ne("state",8);
        List<SysOrderItem> itemList = orderItemService.list(itemQueryWrapper);
        if(itemList.size()==0){
            order.setState(8);
            orderService.updateById(order);
        }

        return ResultUtil.success();
    }

    @RequestMapping("edit")
    @ResponseBody
    public ResponseBean add(GoodsAppraise goodsAppraise){
        if(goodsAppraise==null){
            return  ResultUtil.error();
        }
        //保存到数据库
        goodsAppraiseService.save(goodsAppraise);

        return ResultUtil.successData(goodsAppraise);
    }




    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, GoodsAppraise queryParam) {
        QueryWrapper<GoodsAppraise> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(queryParam.getUserId()!=null,"user_id",queryParam.getUserId());
        queryWrapper.eq(queryParam.getGoodsId()!=null,"goods_id",queryParam.getGoodsId());
        queryWrapper.eq(queryParam.getOrderItemId()!=null,"order_item_id",queryParam.getOrderItemId());
        queryWrapper.eq(queryParam.getOrderId()!=null,"order_id",queryParam.getOrderId());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getUserName()),"user_name",queryParam.getUserName());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getGoodsName()),"goods_name",queryParam.getGoodsName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<GoodsAppraise> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<GoodsAppraise> listPage = goodsAppraiseService.page(indexPage, queryWrapper);
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
        goodsAppraiseService.removeByIds(idList);
        return ResultUtil.success();
    }

}
