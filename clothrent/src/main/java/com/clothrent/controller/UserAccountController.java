package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.UserAccount;
import com.clothrent.service.UserAccountService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户账户表 前端控制器
 * </p>
 *
 * @author liuqiming
 * @since 2024-03-01
 */
@Controller
@RequestMapping("/userAccount")
public class UserAccountController {


    private static  final Logger logger= LoggerFactory.getLogger(UserAccountController.class);

    @Autowired
    private UserAccountService userAccountService;

    @RequestMapping("/info/{id}")
    @ResponseBody
    public ResponseBean info(@PathVariable Long id) {
        return ResultUtil.successData(userAccountService.getById(id));
    }



    @RequestMapping("/listPage")
    public String listPage() {
        return "admin/userAccount/list";
    }


    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, UserAccount queryParam ) {
        QueryWrapper<UserAccount> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(queryParam.getOrderId()!=null,"order_id",queryParam.getOrderId());
        queryWrapper.eq(queryParam.getOrderNum()!=null,"order_num",queryParam.getOrderNum());
        queryWrapper.eq(queryParam.getUserId()!=null,"user_id",queryParam.getUserId());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getUserName()),"user_name",queryParam.getUserName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<UserAccount> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<UserAccount> listPage = userAccountService.page(indexPage, queryWrapper);
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
        userAccountService.removeByIds(idList);
        return ResultUtil.success();
    }

}
