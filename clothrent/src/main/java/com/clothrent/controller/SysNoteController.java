package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ComCodeEnum;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysFile;
import com.clothrent.entity.SysGoods;
import com.clothrent.entity.SysNote;
import com.clothrent.entity.SysUser;
import com.clothrent.service.SysFileService;
import com.clothrent.service.SysGoodsService;
import com.clothrent.service.SysNoteService;
import com.clothrent.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
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
@RequestMapping({"/note","home/note","wear","home/wear"})
public class SysNoteController {

    private static  final Logger logger= LoggerFactory.getLogger(SysNoteController.class);

    @Autowired
    private SysNoteService noteService;

    @Autowired
    SysUserService userService;

    @Autowired
    SysGoodsService goodsService;

    @Autowired
    SysFileService fileService;

    @ModelAttribute
    public void  addModelInfo(Model model){
        List<SysGoods> goodsList = goodsService.list();
        model.addAttribute("goodsList",goodsList);
    }

    @RequestMapping("detailInfo")
    @ResponseBody
    public ResponseBean detailInfo(Long id){
        SysNote sysNote = noteService.getById(id);
        return ResultUtil.successData(sysNote);
    }


    /**
     * 跳转前台我的笔记列表页面
     * @return
     */
    @RequestMapping("/myhomeListPage")
    public String myhomeListPage() {
        return "home/note/mylist";
    }

    /**
     * 跳转到前台详情页
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/detail/{id}")
    public String wearDetail(@PathVariable Long id, Model model,String type) {
        model.addAttribute("noteId",id);
        model.addAttribute("type",type);
        SysNote sysNote = noteService.getById(id);
        if(StringUtils.isEmpty(type)){
            type=sysNote.getType();
        }
        Long goodsId = sysNote.getGoodsId();
        if(goodsId!=null){
            SysGoods sysGoods = goodsService.getById(goodsId);
            model.addAttribute("goods",sysGoods);
        }
        model.addAttribute("note", sysNote);
        model.addAttribute("noteNext", sysNote);
        model.addAttribute("notePriv", sysNote);
        SysNote sysNotice1 = noteService.getById(id + 1);
        if(sysNotice1!=null){
            model.addAttribute("noteNext", sysNotice1);
        }
        SysNote sysNotice2 = noteService.getById(id - 1);
        if(sysNotice2!=null){
            model.addAttribute("notePriv", sysNotice2);
        }

        return "home/note/detail";
    }




    /**
     * 跳转到前台发布笔记页面
     * @return
     */
    @RequestMapping("publishPage")
    public String publishPage(String type,Model model){
        model.addAttribute("type",type);
        return "home/note/publish";
    }


    /**
     * 跳转到增加页面
     * @return
     */
    @RequestMapping("addPage")
    public String addPage(String type,Model model){
        model.addAttribute("type",type);
        return "/home/note/add";

    }

    // 发布 添加
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(SysNote note, HttpSession session, MultipartFile file){
        logger.debug("addSysNote:"+note);
        if(note==null){
            return  ResultUtil.error();
        }
        SysUser loginUser = ToolsUtils.getLoginUser(session);
        note.setUserId(loginUser.getId());
        note.setUserName(loginUser.getName());
        noteService.save(note);
        //保存文件并更新saveName到user.field1
        if (file!=null&&!file.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(note.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_note);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                note.setField1(saveFile.getSaveName());
            }
            noteService.updateById(note);
        }

        return ResultUtil.successData(note);
    }

    /**
     * 跳转到修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/editPage/{id}")
    public String editPage(@PathVariable Long id, Model model,String type) {
        model.addAttribute("note", noteService.getById(id));
        return "/home/note/edit";
    }


    /**
     * 修改
     * @param note
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public ResponseBean updateById(SysNote note, HttpSession session) {
        if (note == null) {
            return  ResultUtil.error();
        }
        note.setUpdateTime(LocalDateTime.now());
        String type = note.getType();
        if("2".equals(type)){//说明是穿搭
            Long goodsId = note.getGoodsId();
            if(goodsId!=null){
                SysGoods sysGoods = goodsService.getById(goodsId);
                note.setGoodsName(sysGoods.getName());
            }
        }
        noteService.updateById(note);
        return ResultUtil.successData(note);
    }


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String noteListPage(String type,Model model) {
        return "admin/note/list";
    }

    /**
     * 跳转前台列表页面
     * @return
     */
    @RequestMapping("/homeListPage")
    public String homeListPage(String type,Model model) {
        return "home/note/list";
    }

    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysNote queryParam , HttpSession session, String beginTime, String endTime) {
        QueryWrapper<SysNote> queryWrapper=new QueryWrapper<>();

        queryWrapper.eq(queryParam.getUserId()!=null,"user_id",queryParam.getUserId());
        queryWrapper.eq(queryParam.getType()!=null,"type",queryParam.getType());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getTitle()),"title",queryParam.getTitle());
        queryWrapper.eq(!StringUtils.isEmpty(queryParam.getCategory()),"category",queryParam.getCategory());
        queryWrapper.ge(!StringUtils.isEmpty(beginTime),"create_time",beginTime);
        queryWrapper.le(!StringUtils.isEmpty(endTime),"create_time",endTime);
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysNote> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysNote> listPage = noteService.page(indexPage, queryWrapper);
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
        noteService.removeByIds(idList);
        return ResultUtil.success();
    }


}
