package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.UserGoods;
import com.clothrent.service.SysFileService;
import com.clothrent.service.SysUserService;
import com.clothrent.service.UserGoodsService;
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
 * 用户浏览表 前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping("/userGoods")
public class UserGoodsController {

    private static  final Logger logger= LoggerFactory.getLogger(UserGoodsController.class);

    @Autowired
    UserGoodsService userGoodsService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysFileService fileService;


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String listPage() {
        return "admin/userGoods/list";
    }

    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询用户浏览记录列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, UserGoods queryParam , HttpSession session) {
        QueryWrapper<UserGoods> queryWrapper=new QueryWrapper<>();
        queryWrapper.allEq(ToolsUtils.convertObjToMap(queryParam,false),false);
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<UserGoods> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<UserGoods> listPage = userGoodsService.page(indexPage, queryWrapper);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 用户浏览记录删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        userGoodsService.removeByIds(idList);
        return ResultUtil.success();
    }

}
