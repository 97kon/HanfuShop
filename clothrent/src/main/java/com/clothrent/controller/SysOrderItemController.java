package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysOrderItem;
import com.clothrent.service.SysFileService;
import com.clothrent.service.SysOrderItemService;
import com.clothrent.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 订单明细表 前端控制器
 * </p>
 *
 * @author liuqiming
 * @since 2024-02-06
 */
@Controller
@RequestMapping({"/item","/home/item"})
public class SysOrderItemController {


    private static  final Logger logger= LoggerFactory.getLogger(SysOrderItemController.class);

    @Autowired
    SysOrderItemService orderItemService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysFileService fileService;


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String orderItemListPage(Long orderId,Model model) {
        model.addAttribute("orderId",orderId);
        return "admin/order/list_item";
    }


    @RequestMapping("/appraisePage")
    public String appraisePage(Long orderItemId,Model model) {
        System.out.println("执行到1");
        SysOrderItem orderItem = orderItemService.getById(orderItemId);
        System.out.println("执行到2");
        model.addAttribute("orderItemId",orderItemId);
        model.addAttribute("orderItem",orderItem);
        System.out.println("执行到3");
        return "home/order/appraise";
    }







    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询订单明细")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysOrderItem queryParam , HttpSession session) {
        logger.debug("查询订单明细参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<SysOrderItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(queryParam.getOrderId()!=null,"order_id",queryParam.getOrderId());
        queryWrapper.eq(queryParam.getState()!=null,"state",queryParam.getState());
        queryWrapper.eq(!StringUtils.isEmpty(queryParam.getCode()),"code",queryParam.getCode());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getUserName()),"user_name",queryParam.getUserName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysOrderItem> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysOrderItem> listPage = orderItemService.page(indexPage, queryWrapper);
        logger.debug("获取的订单明细："+listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

}
