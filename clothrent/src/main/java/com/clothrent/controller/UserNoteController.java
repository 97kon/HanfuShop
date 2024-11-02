package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysNote;
import com.clothrent.entity.SysUser;
import com.clothrent.entity.UserNote;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysNoteService;
import com.clothrent.service.SysUserService;
import com.clothrent.service.UserNoteService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 帖子表 前端控制器
 * </p>
 *
 * @author liuqiming
 * @since 2023-11-18
 */
@Controller
@RequestMapping({"/userNote","home/userNote"})
public class UserNoteController {

    private static  final Logger logger= LoggerFactory.getLogger(UserNoteController.class);

    @Autowired
    private UserNoteService userNoteService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysGoodsService goodsService;

    @Autowired
    SysNoteService noteService;


    // 发布 添加
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(@RequestBody UserNote userNote, HttpSession session, Long privUserId){
        logger.debug("addUserNote:"+userNote);
        if(userNote==null){
            return  ResultUtil.error();
        }
        SysUser loginUser = ToolsUtils.getLoginUser(session);
        if(loginUser==null){
            return ResultUtil.error("当前用户未登录，请先登录！");
        }
        Long noteId = userNote.getNoteId();
        SysNote sysNote = noteService.getById(noteId);
        userNote.setNoteTitle(sysNote.getTitle());
        userNote.setNoteImg(sysNote.getField1());
        userNote.setUserId(loginUser.getId());
        userNote.setUserName(loginUser.getName());
        userNote.setUserImg(loginUser.getField1());



        Integer actionType = userNote.getActionType();
        // 判断用户是否点赞收藏过，如果是，则取消点赞收藏，否则添加点赞收藏
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",loginUser.getId());
        queryWrapper.eq("note_id",noteId);
        queryWrapper.eq("action_type",actionType);
        List<UserNote> checkList = userNoteService.list(queryWrapper);
        switch (actionType){
            case 1:
                if(checkList!=null&&!checkList.isEmpty()){
                    userNoteService.remove(queryWrapper);
                    sysNote.setFavorNum(sysNote.getFavorNum()-1);
                }else{
                    userNoteService.save(userNote);
                    logger.debug("用户点赞添加结果：{}",userNote);
                    sysNote.setFavorNum(sysNote.getFavorNum()+1);
                }
                break;
            case 2:

                if(checkList!=null&&!checkList.isEmpty()){
                    userNoteService.remove(queryWrapper);
                    sysNote.setCollectNum(sysNote.getCollectNum()-1);
                }else{
                    userNoteService.save(userNote);
                    logger.debug("用户收藏添加结果：{}",userNote);
                    sysNote.setCollectNum(sysNote.getCollectNum()+1);
                }
                break;
            case 3:
                //回复根Id
                Long replyId = userNote.getReplyId();
                Long replyUserId = userNote.getReplyUserId();//

                if(replyUserId!=null&& privUserId!=null){
                    SysUser replyUser = userService.getById(replyUserId);//回复人
                    userNote.setReplyUserName(replyUser.getName());
                    userNote.setUserImg(replyUser.getField1());
                    //获取根对象
                    UserNote replyRootNote = userNoteService.getById(replyId);
                    userNote.setNoteId(replyRootNote.getNoteId());
                    userNote.setNoteTitle(replyRootNote.getNoteTitle());
                    userNote.setNoteImg(replyRootNote.getNoteImg());
                    //获取被回复人信息
                    SysUser privUser = userService.getById(privUserId);
                    userNote.setUserId(privUser.getId());//被回复人的UserId
                    userNote.setUserName(privUser.getName());

                }
                userNoteService.save(userNote);
                logger.debug("用户笔记动作添加结果：{}",userNote);
                sysNote.setCommentNum(sysNote.getCommentNum()+1);

                break;
        }
        sysNote.setTotalNum(sysNote.getFavorNum()+sysNote.getCollectNum()+sysNote.getCommentNum());
        noteService.updateById(sysNote);

        return ResultUtil.successData(userNote);
    }


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String userNoteListPage(Integer actionType) {
        switch (actionType){
            case 1:return "admin/userNote/favor_list";//用户点赞列表
            case 2:return "admin/userNote/collect_list";//用户收藏列表
            case 3:return "admin/userNote/comment_list";//用户评论列表
        }
        return "admin/userNote/favor_list";
    }

    /**
     * 跳转前台用户我的收藏列表界面
     * @return
     */
    @RequestMapping("/myhomeListPage")
    public String myhomeListPage() {
        return "home/userNote/mylist";
    }

    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询用户笔记动作列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, UserNote queryParam , HttpSession session, Integer child, String beginTime, String endTime) {
        QueryWrapper<UserNote> queryWrapper=new QueryWrapper<>();
        if(child!=null&&child==1){
            queryWrapper.isNull("reply_id");
        }
        queryWrapper.eq(queryParam.getNoteId()!=null,"note_id",queryParam.getNoteId());
        queryWrapper.eq(queryParam.getUserId()!=null,"user_id",queryParam.getUserId());
        queryWrapper.ge(!StringUtils.isEmpty(beginTime),"create_time",beginTime);
        queryWrapper.le(!StringUtils.isEmpty(endTime),"create_time",endTime);
        queryWrapper.eq(queryParam.getActionType()!=null,"action_type",queryParam.getActionType());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getNoteTitle()),"note_title",queryParam.getNoteTitle());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<UserNote> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<UserNote> listPage = userNoteService.page(indexPage, queryWrapper);
        logger.debug("获取的用户笔记动作列表：{}",listPage);
        if(child!=null&&child==1){
            List<UserNote> userNoteList = listPage.getRecords();
            for(UserNote userNote:userNoteList){
                queryWrapper=new QueryWrapper<>();
                queryWrapper.eq("reply_id",userNote.getId());
                queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
                List<UserNote> childList = userNoteService.list(queryWrapper);
                userNote.setChildList(childList);
            }
        }
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 用户笔记动作删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        userNoteService.removeByIds(idList);
        return ResultUtil.success();
    }


}
