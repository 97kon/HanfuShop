package com.clothrent.common.file;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 文件处理工具类
 */
public class FileUtils{
    private static  final Logger logger= LoggerFactory.getLogger(FileUtils.class);
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension)
    {
        for (String str : allowedExtension)
        {
            if (str.equalsIgnoreCase(extension))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     * @return 后缀名 不带.
     */
    public static  String getExtension(String fileName){
        if (fileName == null) {
            return null;
        }
        return FilenameUtils.getExtension(fileName);
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file)
    {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)){
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    /**
     * 输出指定文件的byte数组
     * 
     * @param filePath 文件路径
     * @param os 输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException
    {
        FileInputStream fis = null;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0)
            {
                os.write(b, 0, length);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath)
    {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists())
        {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     * 
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename)
    {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 下载文件名重新编码
     * @param request 请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
    {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE"))
        {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else if (agent.contains("Firefox"))
        {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        }
        else if (agent.contains("Chrome"))
        {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        else
        {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    public static void copyFile(InputStream in, String destAllPath) {
        FileOutputStream outputStream=null;
        try {
            File file = new File(destAllPath);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            if (!file.exists()){
                file.createNewFile();
            }else{
                file.delete();
                file.createNewFile();
            }
            outputStream=new FileOutputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = in.read(b)) !=-1){
                outputStream.write(b, 0, length);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //获取网络图片保存到本地
    public static String saveFileByUrl(String imgUrl,String baseFilePath) throws IOException {
        logger.debug("当前要获取的图片url:{}",imgUrl);
        FileOutputStream out = null;
        BufferedInputStream in = null;
        HttpURLConnection connection = null;
        byte[] buf = new byte[10240];
        int len = 0;
        String saveFileName= "";
        try {
            String redirectUrl = getRedirectUrl(imgUrl);
            if(StringUtils.isEmpty(redirectUrl)){
                redirectUrl=imgUrl;
            }
            String extension=StringUtils.isEmpty(getExtension(redirectUrl))?"jpg":getExtension(redirectUrl);
            saveFileName= UUID.randomUUID().toString().replaceAll("-","")+"."+extension;
            URL url = new URL(redirectUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(15000);
            connection.connect();
            in = new BufferedInputStream(connection.getInputStream());
            File saveFile=new File(baseFilePath+File.separator+saveFileName);
            if(!saveFile.getParentFile().exists()){
                saveFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(saveFile);
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            logger.debug("字节流大小：{}...........",out.getChannel().size());
            out.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            logger.error("读取网络url出错：{}", imgUrl);
        } finally {
            try {
                in.close();
                out.close();
                connection.disconnect();
            } catch (IOException e) {
                logger.error("读取网络url出错：{}", imgUrl);
                logger.error(e.getMessage());
            }
        }
        return  saveFileName;
    }

    //获取重定向后的URL
    private static String getRedirectUrl(String path){
        HttpURLConnection connection=null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection)url.openConnection();
            //设置是否请求自动处理重定向，设置为false，自己获取重定向url进行解析
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(15000);
            //从响应头里面获取Location，也就是目标重定向地址
            String location = connection.getHeaderField("Location");
            logger.debug("connection.getHeaderField(\"Location\")：{}",location);
            return location;
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            connection.disconnect();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String fileurl="http://img3m6.ddimg.cn/46/26/11043133246-1_u_1.jpg";
        String baseFilePath="D://myfilemapping/bookshop/file";
        saveFileByUrl(fileurl,baseFilePath);
    }
}
