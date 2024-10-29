package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.UserAddress;
import com.clothrent.service.SysFileService;
import com.clothrent.service.SysUserService;
import com.clothrent.service.UserAddressService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户收货地址表 前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping({"/userAddress","/home/userAddress"})
public class UserAddressController {

    private static  final Logger logger= LoggerFactory.getLogger(UserAddressController.class);

    @Autowired
    UserAddressService userAddressService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysFileService fileService;


    // 跳到我的收货地址页面
    @RequestMapping("/detail")
    public String myAddressPage(){
        return "home/address/detail";
    }

    // 设置为默认地址
    @RequestMapping("updateDefault")
    @ResponseBody
    public ResponseBean updateDefault(Long userId, Long id){
        UpdateWrapper<UserAddress> updateWrapper=new UpdateWrapper();
        updateWrapper.eq("user_id",userId).set("is_default",0);
        userAddressService.update(updateWrapper);
        updateWrapper=new UpdateWrapper();
        updateWrapper.eq("id",id).set("is_default",1);
        userAddressService.update(updateWrapper);
        return ResultUtil.success();
    }


    // 保存或者更新用户地址
    @RequestMapping("saveOrUpdate")
    @ResponseBody
    public ResponseBean saveOrUpdate(UserAddress userAddress, HttpSession session){
        logger.debug("addUserAddress:"+userAddress);
        if(userAddress==null){
            return  ResultUtil.error();
        }
        Long id = userAddress.getId();
        if(id==null){
            userAddress.setUserId(ToolsUtils.getLoginUserId(session));
            userAddress.setUserName(ToolsUtils.getLoginUserName(session));
            userAddressService.save(userAddress);
        }else{
            userAddress.setUpdateTime(LocalDateTime.now());
            userAddressService.updateById(userAddress);
        }
        logger.debug("用户收货地址添加或更新结果："+userAddress);

        return ResultUtil.successData(userAddress);
    }

    // 用户收货地址发布 添加
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(UserAddress userAddress, HttpSession session){
        logger.debug("addUserAddress:"+userAddress);
        if(userAddress==null){
            return  ResultUtil.error();
        }
        userAddress.setUserId(ToolsUtils.getLoginUserId(session));
        userAddress.setUserName(ToolsUtils.getLoginUserName(session));
        userAddressService.save(userAddress);
        logger.debug("用户收货地址添加结果："+userAddress);

        return ResultUtil.successData(userAddress);
    }




    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询用户收货地址列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, UserAddress queryParam , HttpSession session, String beginDate, String endDate) {
        logger.debug("查询用户收货地址列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<UserAddress> queryWrapper=new QueryWrapper<>();
        queryWrapper.allEq(ToolsUtils.convertObjToMap(queryParam,false),false);
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), "is_default");
        IPage<UserAddress> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<UserAddress> listPage = userAddressService.page(indexPage, queryWrapper);
        logger.debug("获取的用户收货地址列表："+listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 用户收货地址删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        userAddressService.removeByIds(idList);
        return ResultUtil.success();
    }

}
