package com.clothrent.controller;

import com.clothrent.common.CommonEnum;
import com.clothrent.common.ResponseBean;
import com.clothrent.common.ResultUtil;
import com.clothrent.common.file.FileUtils;
import com.clothrent.entity.SysFile;
import com.clothrent.mapper.SysFileMapper;
import com.clothrent.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.UUID;

@Api(tags = {"文件管理"})
@Controller
@RequestMapping({"file","ueditor","/app/file"})
public class FileController {

    private static  final Logger logger= LoggerFactory.getLogger(FileController.class);

    @Autowired
    private SysFileService fileService;

    @Value("${com.jane.file.baseFilePath}")
    private String baseFilePath;

    @Value("${com.jane.configjson.baseFilePath}")
    private String configJsonPath;

    @Autowired
    private SysFileMapper fileMapper;



    /**
     * 文件上传后删除所属对象的原先文件
     * @param sysFile
     * @param file
     * @return
     */
    @ApiOperation(value="文件上传后删除所属对象的原先文件")
    @RequestMapping(value = "/uploadAndDel",method = {RequestMethod.POST})
    @ResponseBody
    public ResponseBean fileUpload(SysFile sysFile, MultipartFile file){
        if(sysFile==null||file==null||file.isEmpty()){
            logger.debug("文件上传参数不对！");
            return new ResponseBean(false, CommonEnum.BAD_REQUEST);
        }
        SysFile uploadFile = fileService.saveOrUpdateFile(sysFile, file);

        if(uploadFile==null){
            return new ResponseBean(false,CommonEnum.BAD_REQUEST);
        }
        return  ResultUtil.successData(uploadFile);

    }

    /**
     * 只上传文件，返回保存的文件对象，并不会对原先文件进行删除
     * @param sysFile
     * @param file
     * @return
     */
    @ApiOperation(value="文件上传并返回保存的文件对象")
    @RequestMapping(value = "/uploadOnly",method = {RequestMethod.POST})
    @ResponseBody
    public ResponseBean fileUploadOnly(SysFile sysFile, @RequestParam(value = "file",required = true) MultipartFile file){
        if(sysFile==null||file==null||file.isEmpty()){
            logger.debug("文件上传参数不对！");
            return ResultUtil.error(CommonEnum.BAD_PARAM);
        }
        String originalFilename = file.getOriginalFilename();
        long size = file.getSize();
        String extendName = "."+ FileUtils.getExtension(file);
        logger.debug("file.getResource:"+file.getResource());
        String saveFileName= UUID.randomUUID().toString().replaceAll("-","")+extendName;
        File saveFile=new File(baseFilePath+File.separator+saveFileName);
        if(!saveFile.getParentFile().exists()){
            saveFile.getParentFile().mkdirs();
        }
        try {
            //保存文件到最终路径
            file.transferTo(saveFile);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        sysFile.setFileName(originalFilename);
        sysFile.setFileType(file.getContentType());
        sysFile.setFileUrl(baseFilePath+File.separator+saveFileName);
        sysFile.setFileSize(size+"");
        sysFile.setSaveName(saveFileName);
        int insert = fileMapper.insert(sysFile);
        sysFile.setField1("/file/fileDown?saveName="+saveFileName);

        if(sysFile==null){
            return ResultUtil.error(CommonEnum.BAD_REQUEST);
        }
        return ResultUtil.successData(sysFile);

    }


    /**
     * ueditor文件回显
     * @param request
     * @return
     */
    @RequestMapping("/jsp/upload")
    public ResponseEntity<byte[]> ueditorfileDown(HttpServletRequest request, String filePath){
        logger.debug("filePath:"+filePath);
        byte [] body = null;
        String rootPath = baseFilePath;
//        String rootPath = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"static";
        String requestURL = request.getRequestURL().toString();
        if(requestURL.contains("127.0.0.1")||requestURL.contains("localhost")||requestURL.contains("192.168")){
            logger.debug("本地环境");
        }else{
            rootPath=configJsonPath;
        }
        String fileUrl=rootPath+ File.separator+filePath;
        try {
            //获取到文件流
            InputStream in = new FileSystemResource(fileUrl).getInputStream();
            body = new byte[in.available()];
            in.read(body);
        } catch (IOException e1) {
            logger.debug("文件读入出错，文件路径为："+fileUrl);
            e1.printStackTrace();
        }
        int index = filePath.lastIndexOf("/");
        String fileName = filePath.substring(index+1);
        logger.debug("本次下载的文件为："+fileName);
        //添加响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename="+fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(body, headers, statusCode);
        return response;
    }
    /**
     *
     * @param fileName--下载文件名
     * @param saveName--文件实际保存的名字
     * @return
     */
    @ApiOperation(value = "文件下载/回显",notes = "统一文件下载/回显接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "saveName",value = "文件名字",required = true),
            @ApiImplicitParam(name = "fileName",value = "下载展示文件名字")
    })
    @RequestMapping(value = "/fileDown",method =  {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<byte[]> fileDown(String saveName, String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        logger.debug("saveName:"+saveName);
        byte [] body = null;
        String fileUrl=baseFilePath+ File.separator+saveName;
        try {
            //获取到文件流
            InputStream in = new FileSystemResource(fileUrl).getInputStream();
            body = new byte[in.available()];
            in.read(body);
        } catch (IOException e1) {
            logger.debug("文件读入出错，文件路径为："+fileUrl);
//            e1.printStackTrace();
        }
        if(StringUtils.isEmpty(fileName)){
            fileName=saveName;
        }
        //添加响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename="+ FileUtils.setFileDownloadHeader(request, fileName));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(body, headers, statusCode);
        return response;
    }

    /**
     * 第二种文件下载/回显方法
     * @param id tb_sys_file.id
     * @param fileName  实际下载的文件名字
     * @return
     */
    @ApiOperation(value = "根据ID文件下载/回显",notes = "path里面传文件ID")
    @RequestMapping(value = "/fileDown/{id}",method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<byte[]> fileDown(@PathVariable Long id, String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        SysFile sysFile = fileService.getById(id);
        byte [] body = null;
        String fileUrl=sysFile.getFileUrl();
        try {
            //获取到文件流
            InputStream in = new FileSystemResource(fileUrl).getInputStream();
            body = new byte[in.available()];
            in.read(body);
        } catch (IOException e1) {
            logger.debug("文件读入出错，文件路径为："+fileUrl);
            e1.printStackTrace();
        }
        //添加响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename="+ FileUtils.setFileDownloadHeader(request, fileName));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpStatus statusCode = HttpStatus.OK;
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(body, headers, statusCode);
        return response;
    }


    //通用从classpath下载文件文件
    @RequestMapping(value = "/export")
    public ResponseEntity<byte[]> fileExport(HttpServletRequest request,String fileName){
        byte [] body = null;
        InputStream inputStream=null;
        FileInputStream fileInputStream=null;
        try {

            //获取到文件流
            inputStream = FileController.class.getClassLoader().getResourceAsStream(fileName);
            logger.debug("需要导出的模板：{},获取的流为：{}",fileName,inputStream);
//            if(in.available()<1){
//                in=new FileInputStream(new File(baseFilePath+File.separator+fileName));
//            }
            String temppath = System.getProperty("java.io.tmpdir");
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String newFileName=uuid+fileName;
            FileUtils.copyFile(inputStream,temppath+File.separator+newFileName);

            fileInputStream=new FileInputStream(temppath+File.separator+newFileName);
            body = new byte[fileInputStream.available()];
            logger.debug("导出模板大小：{},读入body大小:{}",fileInputStream.available(),body.length);
            fileInputStream.read(body);

            //添加响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename="+ FileUtils.setFileDownloadHeader(request,fileName));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpStatus statusCode = HttpStatus.OK;
            ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(body, headers, statusCode);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return null;
        }finally {
            try {
                inputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

}
