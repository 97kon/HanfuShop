package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysDictValue;
import com.clothrent.service.SysDictValueService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典值表 前端控制器
 * </p>
 *
 */
@Controller
@RequestMapping({"/dictValue","/home/dictValue"})
public class SysDictValueController {
    private static final Logger logger= LoggerFactory.getLogger(SysDictValueController.class);

    @Autowired
    SysDictValueService dictValueService;
    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询系统参数列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysDictValue queryParam , HttpSession session) {
        logger.debug("查询系统参数列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<SysDictValue> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(queryParam.getDictCode()!=null,"dict_code",queryParam.getDictCode());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getName()),"name",queryParam.getName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysDictValue> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysDictValue> listPage = dictValueService.page(indexPage, queryWrapper);
        logger.debug("获取的系统参数列表：{}",listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 跳转到增加页面
     * @return
     */
    @RequestMapping("addPage")
    public String addPage(){
        return "admin/dictValue/add";
    }

    // 发布 添加
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(SysDictValue dictValue, HttpSession session){
        logger.debug("addSysDictValue:"+dictValue);
        if(dictValue==null){
            return  ResultUtil.error();
        }
        QueryWrapper<SysDictValue> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("dict_code",dictValue.getDictCode());
        queryWrapper.eq("dict_value",dictValue.getDictValue());
        List<SysDictValue> checkList = dictValueService.list(queryWrapper);
        if(checkList!=null&&!checkList.isEmpty()){
            return ResultUtil.error("当前数据字典值已经存在！");
        }
        dictValueService.save(dictValue);
        logger.debug("数据字典添加结果：",dictValue);

        return ResultUtil.successData(dictValue);
    }

    /**
     * 跳转到数据字典修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/editPage/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("dictValue", dictValueService.getById(id));
        return "admin/dictValue/edit";
    }


    /**
     * 数据字典修改
     * @param dictValue
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public ResponseBean updateById(SysDictValue dictValue, HttpSession session) {
        if (dictValue == null) {
            return  ResultUtil.error();
        }

        QueryWrapper<SysDictValue> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("dict_code",dictValue.getDictCode());
        queryWrapper.eq("dict_value",dictValue.getDictValue())
                .ne("id",dictValue.getId());
        List<SysDictValue> checkList = dictValueService.list(queryWrapper);
        if(checkList!=null&&!checkList.isEmpty()){
            return ResultUtil.error("当前数据字典值已经存在！");
        }
        dictValue.setUpdateTime(LocalDateTime.now());
        dictValueService.updateById(dictValue);
        return ResultUtil.successData(dictValue);
    }


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String dictValueListPage() {
        return "admin/dictValue/list";
    }



    /**
     * 数据字典删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        dictValueService.removeByIds(idList);
        return ResultUtil.success();
    }

}
