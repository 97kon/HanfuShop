package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.UserScore;
import com.clothrent.service.SysUserService;
import com.clothrent.service.UserScoreService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会员积分表 前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping("/score")
public class UserScoreController {

    private static  final Logger logger= LoggerFactory.getLogger(UserScoreController.class);

    @Autowired
    private UserScoreService scoreService;

    @Autowired
    SysUserService userService;



    // 添加
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(UserScore score, HttpSession session){
        logger.debug("addUserScore:"+score);
        if(score==null){
            return  ResultUtil.error();
        }
        score.setUserId(ToolsUtils.getLoginUserId(session));
        score.setUserName(ToolsUtils.getLoginUserName(session));
        scoreService.save(score);
        logger.debug("用户积分添加结果："+score);

        return ResultUtil.successData(score);
    }



    /**
     * 跳转用户积分列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String scoreListInPage() {
        return "admin/score/list";
    }




    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询用户积分列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, UserScore queryParam , HttpSession session) {
        logger.debug("查询用户积分列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<UserScore> queryWrapper=new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getOperatorName()),"operator_name",queryParam.getOperatorName());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getUserName()),"user_name",queryParam.getUserName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<UserScore> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<UserScore> listPage = scoreService.page(indexPage, queryWrapper);
        logger.debug("获取的用户积分列表："+listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }



    /**
     * 用户积分删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        scoreService.removeByIds(idList);
        return ResultUtil.success();
    }


}
