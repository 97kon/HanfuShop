package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.ComCodeEnum;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.ToolsUtils;
import com.clothrent.craw.CrawlJingDongService;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.*;
import com.clothrent.service.*;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @since 2024-09-06
 */
@Controller
@RequestMapping({"/goods","/home/goods"})
public class GoodsController {
    private static  final Logger logger= LoggerFactory.getLogger(GoodsController.class);


    @Autowired
    SysGoodsService goodsService;
    
    @Autowired
    SysCategoryService categoryService;
    
    @Autowired
    SysFileService fileService;

    @Autowired
    SysUserService userService;

    @Autowired
    UserInventoryService inventoryService;

//    @Autowired
//    CrawlJingDongService jingDongService;

    @Value("${com.jane.file.baseFilePath}")
    private String baseFilePath;

    @Autowired
    SysDictValueService dictValueService;

    @Autowired
    CrawlJingDongService crawlJingDongService;

    @Autowired
    UserGoodsService userGoodsService;


    /**
     * 爬虫
     * @param number 每次多少条
     * @return
     */
    @RequestMapping("reptile")
    @ResponseBody
    public ResponseBean updateImg(@RequestParam(required = false)Integer secondCId ,
                                  @RequestParam(required = false,defaultValue = "20") Integer number,
                                  @RequestParam(required = false,defaultValue = "3")Integer threadNum){
        try {
            if(secondCId==null||secondCId==0){
                return ResultUtil.error("请选择具体分类！");
            }
            // 1.依据商品名称进行爬虫；2 如果分类不为空，则依据分类名称
            SysCategory sencondCategory = categoryService.getById(secondCId);
            String keyWord= sencondCategory.getName()+sencondCategory.getParentName();
            crawlJingDongService.reptile(number,"0",keyWord,secondCId);
            return ResultUtil.success("爬取成功！");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return ResultUtil.error("系统异常！");
    }


    /**
     * 跳转到增加页面
     * @return
     */
    @RequestMapping("addPage")
    public String addPage(){
        return "admin/goods/add";
    }

    // 物品发布 添加
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean add(SysGoods goods, HttpSession session,
                            @RequestParam(name="file1",required = false) MultipartFile file1,
                            @RequestParam(name="file2",required = false) MultipartFile file2,
                            @RequestParam(name="file3",required = false) MultipartFile file3,
                            @RequestParam(name="file4",required = false) MultipartFile file4,
                            @RequestParam(name="file5",required = false) MultipartFile file5
    ){
        logger.debug("addSysGoods:"+goods);
        if(goods==null){
            return  ResultUtil.error();
        }
        BigDecimal discount = goods.getDiscount();
        if(discount==null){
            goods.setDiscount(goods.getPrice());
        }
        Long secondCId = goods.getSecondCId();
        if(secondCId!=null){
            SysCategory secondCategory = categoryService.getById(secondCId);
            goods.setSecondCName(secondCategory.getName());
            goods.setCategoryId(secondCategory.getParentId());
            goods.setCategoryName(secondCategory.getParentName());
        }
        SysUser loginUser = ToolsUtils.getLoginUser(session);
        goods.setUserId(loginUser.getId());
        goods.setUserName(loginUser.getName());
        goodsService.save(goods);
        //保存文件并更新saveName到 field1
        if (file1!=null&&!file1.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic1);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file1);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic1(saveFile.getSaveName());
            }
        }
        if (file2!=null&&!file2.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic2);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file2);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic2(saveFile.getSaveName());
            }
        }
        if (file3!=null&&!file3.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic3);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file3);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic3(saveFile.getSaveName());
            }
        }
        if (file4!=null&&!file4.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic4);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file4);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic4(saveFile.getSaveName());
            }
        }
        if (file5!=null&&!file5.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic5);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file5);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic5(saveFile.getSaveName());
            }
        }
        goodsService.updateById(goods);
        logger.debug("物品添加结果："+goods);
        // 初始化商品时 ，插入库存变动记录
        UserInventory userInventory=new UserInventory();
        userInventory.setGoodsId(goods.getId());
        userInventory.setGoodsName(goods.getName());
        userInventory.setUserId(ToolsUtils.getLoginUserId(session));
        userInventory.setUserName(ToolsUtils.getLoginUserName(session));
        userInventory.setCurrentStock(goods.getStock().intValue());
        userInventory.setChangeStock(goods.getStock().intValue());
        userInventory.setBeforeStock(0);
        userInventory.setType(1);// '类型：1 入库；2 出库',
        userInventory.setSupplier("管理员新建商品");
        userInventory.setReason("管理员新建商品");
        userInventory.setPrice(BigDecimal.valueOf(0));
        inventoryService.save(userInventory);

        return ResultUtil.successData(goods);
    }

    /**
     * 跳转到物品修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/editPage/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("goods", goodsService.getById(id));
        return "admin/goods/edit";
    }

    /**
     * 跳转到物品详情页面
     * @param id
     * @param model
     * @return
     */
    @ApiOperation(value = "查看物品详情")
    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model,HttpSession session) {
        SysGoods goods = goodsService.getById(id);
        // 获取当前商品
        model.addAttribute("goods", goods);
        QueryWrapper<SysGoods> queryWrapper=new QueryWrapper<>();
        // 获取热门商品
        queryWrapper.orderBy(true,false, "comment_num");
        IPage<SysGoods> indexPage = new Page<>(1,8);
        IPage<SysGoods> listPage = goodsService.page(indexPage, queryWrapper);
        model.addAttribute("hotList",listPage.getRecords());
        String clothSize = goods.getClothSize();
        model.addAttribute("clothSize",clothSize.split(","));
        //添加到用户浏览记录
        Object user = session.getAttribute("user");
        if(user!=null){//说明已经登录
            SysUser loginUser= (SysUser) user;
            UserGoods userGoods=new UserGoods();
            userGoods.setGoodsId(goods.getId());
            userGoods.setGoodsName(goods.getName());
            userGoods.setUserId(loginUser.getId());
            userGoods.setUserName(loginUser.getName());
            userGoodsService.save(userGoods);
        }
        return "home/goods/detail";
    }

    /**
     * 物品修改
     * @param goods
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public ResponseBean updateById(SysGoods goods, HttpSession session,
                                   @RequestParam(name="file1",required = false) MultipartFile file1,
                                   @RequestParam(name="file2",required = false) MultipartFile file2,
                                   @RequestParam(name="file3",required = false) MultipartFile file3,
                                   @RequestParam(name="file4",required = false) MultipartFile file4,
                                   @RequestParam(name="file5",required = false) MultipartFile file5
    ) {
        if (goods == null) {
            return  ResultUtil.error();
        }
        BigDecimal discount = goods.getDiscount();
        if(discount==null){
            goods.setDiscount(goods.getPrice());
        }
        Long secondCId = goods.getSecondCId();
        if(secondCId!=null){
            SysCategory secondCategory = categoryService.getById(secondCId);
            goods.setSecondCName(secondCategory.getName());
            goods.setCategoryId(secondCategory.getParentId());
            goods.setCategoryName(secondCategory.getParentName());
        }
//        goods.setCategoryName(categoryService.getById(goods.getCategoryId()).getName());
        //保存文件并更新saveName到 field1
        if (file1!=null&&!file1.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic1);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file1);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic1(saveFile.getSaveName());
            }
        }
        if (file2!=null&&!file2.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic2);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file2);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic2(saveFile.getSaveName());
            }
        }
        if (file3!=null&&!file3.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic3);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file3);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic3(saveFile.getSaveName());
            }
        }
        if (file4!=null&&!file4.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic4);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file4);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic4(saveFile.getSaveName());
            }
        }
        if (file5!=null&&!file5.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(goods.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_sub_pic5);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file5);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                goods.setGoodsPic5(saveFile.getSaveName());
            }
        }
        goods.setUpdateTime(LocalDateTime.now());
        goodsService.updateById(goods);
        return ResultUtil.successData(goods);
    }


    /**
     * 跳转列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String goodsListPage() {
        return "admin/goods/list";
    }

    /**
     * 跳转前台物品列表页面
     * @return
     */
    @RequestMapping("/homeListPage")
    public String homeListPage(Long categoryId,Model model,String name,String secondCCode,String catgegoryCode) {
        if(categoryId!=null){
            model.addAttribute("categoryId",categoryId);
        }
        if(!StringUtils.isEmpty(secondCCode)){
            model.addAttribute("secondCCode",secondCCode);
        }
        if(!StringUtils.isEmpty(catgegoryCode)){
            model.addAttribute("catgegoryCode",catgegoryCode);
        }
        if(!StringUtils.isEmpty(name)){
            model.addAttribute("name",name);
        }
        return "home/goods/list";
    }

    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * @return
     */
    @ApiOperation(value = "查询物品列表")
    @RequestMapping(value = "/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysGoods queryParam , HttpSession session, String beginDate, String endDate, String vagueName) {
        logger.debug("查询物品列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<SysGoods> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(queryParam.getId()!=null,"id",queryParam.getId());
        queryWrapper.eq(queryParam.getCategoryId()!=null,"category_id",queryParam.getCategoryId());
        queryWrapper.eq(queryParam.getSecondCId()!=null,"second_c_id",queryParam.getSecondCId());
        queryWrapper.eq(!StringUtils.isEmpty( queryParam.getCatgegoryCode()),"catgegory_code",queryParam.getCatgegoryCode());
        queryWrapper.eq(!StringUtils.isEmpty( queryParam.getCategoryName()),"catgegory_name",queryParam.getCategoryName());
        queryWrapper.eq(!StringUtils.isEmpty( queryParam.getSecondCCode()),"second_c_code",queryParam.getSecondCCode());
        queryWrapper.eq(!StringUtils.isEmpty( queryParam.getSecondCName()),"second_c_name",queryParam.getSecondCName());
        queryWrapper.eq(queryParam.getState()!=null,"state",queryParam.getState());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getName()),"name",queryParam.getName());
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysGoods> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysGoods> listPage = goodsService.page(indexPage, queryWrapper);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }


    /**
     * 上下架物品
     * @param id
     * @param state
     * @return
     */
    @RequestMapping("updateState")
    @ResponseBody
    public ResponseBean updateState(Long id,Integer state){
        SysGoods sysGoods = goodsService.getById(id);
        sysGoods.setState(state);
        sysGoods.setUpdateTime(LocalDateTime.now());
        goodsService.updateById(sysGoods);
        return ResultUtil.successData(sysGoods);
    }

    /**
     * 物品删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        goodsService.removeByIds(idList);
        return ResultUtil.success();
    }

}
