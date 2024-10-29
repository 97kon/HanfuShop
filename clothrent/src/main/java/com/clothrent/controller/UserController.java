package com.clothrent.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clothrent.common.*;
import com.clothrent.dto.PageDTO;
import com.clothrent.entity.SysFile;
import com.clothrent.entity.SysUser;
import com.clothrent.service.SysFileService;
import com.clothrent.service.SysUserService;
import io.swagger.annotations.Api;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Api(tags = {"用户管理"})
@Controller
@RequestMapping({"/user","home/user"})
public class UserController {

    private static  final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${com.jane.security.md5.key}")
    private String md5Key;

    @Autowired
    SysFileService fileService;

    @Autowired
    SysUserService userService;


    @Value("${com.jane.file.baseFilePath}")
    private String baseFilePath;

    private static  final String prefix="admin/user";



    @RequestMapping("detailInfo")
    @ResponseBody
    public ResponseBean detailInfo(Long id){
        SysUser sysUser = userService.getById(id);
        return ResultUtil.successData(sysUser);
    }

    /**
     * 跳到前台用户个人中心页面
     * @return
     */
    @RequestMapping("detail")
    public String detail(HttpSession session,Model model){
        // 如果不存在用户，则跳到登录页面
        if(session.getAttribute("user")==null){
            return "home/login";
        }
        model.addAttribute("user", ToolsUtils.getLoginUser(session));
        return "home/user/detail";
    }

    @RequestMapping("addPage")
    public String addPage(Model model){
        return prefix+"/add";
    }




    /**
     * 用户添加
     * @param user
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResponseBean addUser(SysUser user, MultipartFile file){
        logger.debug("addSysUser:"+user);
        if(user==null){
            return  ResultUtil.error(CommonEnum.BAD_PARAM);
        }

        String mobile = user.getMobile();
        if(StringUtils.isEmpty(mobile)||!RegexUtil.checkMobile(mobile)){
            logger.error("手机号为空！");
            return  ResultUtil.error("手机号不合法！");
        }

        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("code",user.getCode());
        List<SysUser> checkList = userService.list(queryWrapper);
        if(checkList!=null&&!checkList.isEmpty()){
            return ResultUtil.error(CommonEnum.USER_CODE_EXIST);
        }

        String password= StringUtils.isEmpty(user.getPassword())?"123456":user.getPassword();
        //对密码进行MD5盐值加密，数据库保存密码的密文
        //MD5Util.md5(password,md5Key  md5key就是盐值
        user.setPassword(MD5Util.md5(password,md5Key));
        boolean add = userService.save(user);
        logger.debug("用户添加结果："+user);
        //保存文件并更新saveName到user.field1
        if (file!=null&&!file.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(user.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_user);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                user.setField1(saveFile.getSaveName());
            }
            userService.updateById(user);
        }
        return  ResultUtil.success();
    }

    /**
     * 跳转到用户修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/editPage/{id}")
    public String editPage(@PathVariable Long id, Model model,HttpSession session) {
        model.addAttribute("user", userService.getById(id));
        return prefix+"/edit";
    }

    /**
     * 跳转到前台用户修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/editFrontPage/{id}")
    public String editFrontPage(@PathVariable Long id, Model model,HttpSession session) {
        model.addAttribute("user", userService.getById(id));
        return "admin/user/edit_front";
    }

    /**
     * 修改用户密码
     * @param id
     * @param password
     * @param oldpwd
     * @return
     */
    @RequestMapping("updatePWd")
    @ResponseBody
    public  ResponseBean updatePWd(Long id,String password,String oldpwd,HttpSession session){
        SysUser user = userService.getById(id);
        String userPassword = user.getPassword();
        if(!MD5Util.md5Verify(oldpwd,md5Key,userPassword)){
            return ResultUtil.error("原始密码不正确！");
        }
        user.setPassword(MD5Util.md5(password,md5Key));
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        session.setAttribute("user",user);
        return ResultUtil.successData(user);
    }


    /**
     * 初始化用户密码
     * @param id
     * @return
     */
    @RequestMapping("initPwd")
    @ResponseBody
    public ResponseBean initPwd(Long id){
        if(id==null||id<1){
            return ResultUtil.error(CommonEnum.BAD_REQUEST);
        }
        SysUser sysUser = userService.getById(id);
        if(sysUser!=null){
            sysUser.setPassword(MD5Util.md5("123456",md5Key));
            sysUser.setUpdateTime(LocalDateTime.now());
            userService.updateById(sysUser);
            return ResultUtil.success();
        }
        return ResultUtil.error(CommonEnum.NO_USER_EXIST);
    }

    /**
     * 用户修改
     * @param user
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public ResponseBean updateById(MultipartFile file, SysUser user,HttpSession session) {
        if (user == null) {
            return ResultUtil.error(CommonEnum.BAD_PARAM);
        }
        String mobile = user.getMobile();
        if(!StringUtils.isEmpty(mobile)&&!RegexUtil.checkMobile(mobile)){
            return ResultUtil.error("手机号不合法！");
        }
        SysUser oldUser = userService.getById(user.getId());
        SysUser loginUser = ToolsUtils.getLoginUser(session);
        if(user.getType()!=null&&oldUser.getType().intValue()!=user.getType().intValue()&&loginUser.getType()!=1){
            return ResultUtil.error("非管理员不可修改用户角色！");
        }

        //处理密码
        String password = user.getPassword();
        if(!StringUtils.isEmpty(password)){
            user.setPassword(MD5Util.md5(password,md5Key));
        }else{
            user.setPassword(null);
        }
        if (file!=null&&!file.isEmpty()) {
            SysFile sysFile=new SysFile();
            sysFile.setObjectId(user.getId());
            sysFile.setCategoryCode(ComCodeEnum.category_code_user);
            SysFile saveFile=fileService.saveOrUpdateFile(sysFile,file);
            if(saveFile!=null&&!StringUtils.isEmpty(saveFile.getSaveName())){
                user.setField1(saveFile.getSaveName());
            }
        }
        user.setUpdateTime(LocalDateTime.now());
        boolean i = userService.updateById(user);
        return ResultUtil.success() ;
    }

    /**
     * 跳转用户列表页面
     * @return
     */
    @RequestMapping("/listPage")
    public String userListPage() {
        return prefix+"/list";

    }


    /**
     * 分页列表查询
     * @param queryParam 查询参数
     * params[beginTime]: 2020-10-16 -map参数
     * params[endTime]: 2020-10-16   -map参数
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public ResponseBean getList(PageDTO pageDTO, SysUser queryParam , HttpSession session) {
        logger.debug("查询用户列表参数："+queryParam+",pageDTO:"+pageDTO);
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        Integer type = ToolsUtils.getLoginUser(session).getType();//用户角色
        if(type==1){
            //说明是管理员，则查询所有
            queryParam.setType(null);
        }else{
            queryWrapper.eq("id",ToolsUtils.getLoginUserId(session));//若是普通用户则只查询自己
        }
        queryWrapper.eq(!StringUtils.isEmpty(queryParam.getCode()),"code",queryParam.getCode());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getName()),"name",queryParam.getName());
        queryWrapper.like(!StringUtils.isEmpty(queryParam.getMobile()),"mobile",queryParam.getMobile());
        queryWrapper.ne("code","admin");//用户管理里面超级管理员admin除外
        queryWrapper.orderBy(true,pageDTO.getIsAsc().equals("asc"), ToolsUtils.camelToUnderline(pageDTO.getOrderByColumn()));
        IPage<SysUser> indexPage = new Page<>(pageDTO.getPageNum(),pageDTO.getPageSize());
        IPage<SysUser> listPage = userService.page(indexPage, queryWrapper);
        logger.debug("获取的用户列表："+listPage);
        //获取分页信息
        Map pageInfo = ToolsUtils.wrapperPageInfo(listPage);
        return ResultUtil.successData(pageInfo);
    }

    /**
     * 用户删除
     * @param idList
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResponseBean delete(@RequestParam("idList") List<Long> idList){
        if(idList==null||idList.isEmpty()){
            return ResultUtil.error("ID不合法！");
        }
        idList.forEach(e->userService.removeById(e));
        return ResultUtil.success();
    }


    /**
     * 启用/禁用用户
     * @param id
     * @param state
     * @return
     */
    @RequestMapping("updateState")
    @ResponseBody
    public ResponseBean updateState(Long id,Integer state){
        SysUser user = userService.getById(id);
        user.setState(state);
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return ResultUtil.successData(user);
    }

    /**
     * 后台用户登录
     * @param user
     * @param session
     * @param verifyCode
     * @return
     */
    @RequestMapping("/checkLogin")
    @ResponseBody
    public ResponseBean checkLogin(SysUser user, HttpSession session, @RequestParam(required = false) String verifyCode) {
        //首先验证验证码是否存在
//        String trueVerifyCode = (String) session.getAttribute("verifyCode");
//        if (trueVerifyCode == null) {
//            return ResultUtil.error(CommonEnum.REFRESH_VERIFYCODE);
//        }
//
//        //判断验证码是否输入正确（验证码忽略大小写）
//        if (!trueVerifyCode.equalsIgnoreCase(verifyCode)) {
//            return ResultUtil.error(CommonEnum.ERROR_VERIFYCODE);
//        }

        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("code",user.getCode());
        queryWrapper.eq(user.getType()!=null,"type",user.getType());
        List<SysUser> list = userService.list(queryWrapper);
        //判断是否存在当前用户
        if (ToolsUtils.isEmpty(list)) {
            return new ResponseBean(false, CommonEnum.NO_USER_EXIST);
        }
        SysUser sysUser = list.get(0);
        if(sysUser.getState()!=1){
            return ResultUtil.error("用户已经被禁用，请联系管理员！");
        }
        //判断密码是否正确
        if (!MD5Util.md5Verify(user.getPassword(), this.md5Key, sysUser.getPassword())) {
            return new ResponseBean(false, CommonEnum.INVALID_PASSWORD);
        }
        //通过所有验证
        session.setAttribute("user", sysUser);
        session.setAttribute("type", sysUser.getType());
        if(sysUser.getType()==1){
            session.setAttribute("admin", sysUser);
        }
        return ResultUtil.successData( sysUser);
    }

}
