package com.clothrent.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.ueditor.ActionEnter;
import com.baidu.ueditor.upload.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 这里contextPath为空！
 */
@Controller
@RequestMapping("/common/plugs/ueditor/jsp")
public class UeditorController {

    private static  final Logger logger= LoggerFactory.getLogger(UeditorController.class);
    /**
     * 读取配置的请求
     * @param request
     * @param response
     */

    @Value("${com.jane.configjson.baseFilePath}")
    private String configJsonPath;


    @Value("${com.jane.file.baseFilePath}")
    private String baseFilePath;

    @RequestMapping("/controller")
    @ResponseBody
    public void getConfigInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        logger.debug("配置文件配置的configJsonPath："+configJsonPath);
        String requestURL = request.getRequestURL().toString();
        logger.debug("requestURL:"+requestURL);
        logger.debug("RequestURI:"+request.getRequestURI());
        logger.debug("ContextPath:"+request.getContextPath());
        String rootPath = baseFilePath;
//        String rootPath = ClassUtils.getDefaultClassLoader().getResource("").getPath()+"static";
        String configPath="/ueditor";
//        String configPath=request.getRequestURI();
        if(requestURL.contains("127.0.0.1")||requestURL.contains("localhost")||requestURL.contains("192.168")){
            logger.debug("本地环境");
        }else{
            rootPath=configJsonPath;
            configPath="/test";//这里随便写
            logger.debug("服务器环境，请将config.json放到"+rootPath+"的上一级下面！");
        }
        logger.debug("rootPath:" + rootPath);
        //将存盘目录设置到request 作用域 在百度上传的类中读取
//        request.setAttribute("uploadPath","");
        /**
         * 1.如果ContextPath为空，则rootPath+RequestURI的父级决定config.json位置
         * 2.如果ContextPath为空不为空 originalPath = this.rootPath + uri.substring(contextPath.length());
         * originalPath.getParentFile()+config.json即为最终完整config.json路径
         */
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();

            String exec = new ActionEnter(new StorageManager(), request, rootPath, configPath).exec();
            /**
             * exec:{"state": "SUCCESS","original": "\u5b9e\u9a8c\u75301.jpg","size": "42389","title": "1580809982781059908.jpg",
             * "type": ".jpg","url": "/ueditor/jsp/upload/image/20200204/1580809982781059908.jpg"}
             */
            System.out.println("exec:" + exec);
            JSONObject parse = JSON.parseObject(exec);
            if(parse.containsKey("url")){
                String oldUrl = parse.getString("url");
                String urlNew="/ueditor/jsp/upload?filePath="+oldUrl;
                parse.put("url",urlNew);
                logger.debug("封装后的exec:"+parse);
            }
            PrintWriter writer = response.getWriter();
            writer.write(parse.toJSONString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
