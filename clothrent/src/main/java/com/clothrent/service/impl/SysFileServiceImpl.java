package com.clothrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clothrent.common.file.FileUtils;
import com.clothrent.entity.SysFile;
import com.clothrent.mapper.SysFileMapper;
import com.clothrent.service.SysFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 附件表，存放文件 服务实现类
 * </p>
 *
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements SysFileService {
    private static  final Logger logger= LoggerFactory.getLogger(SysFileServiceImpl.class);

    @Autowired
    private SysFileMapper fileMapper;


    @Value("${com.jane.file.baseFilePath}")
    private String baseFilePath;

    @Override
    public SysFile saveOrUpdateFile(SysFile sysFile, MultipartFile file) {
        if(sysFile==null||file==null||file.isEmpty()){
            logger.debug("sysFile||file 对象为空！");
            return null;
        }
        String categoryCode = sysFile.getCategoryCode();
        long objectId=sysFile.getObjectId();
        if(StringUtils.isEmpty(categoryCode)||objectId<1){
            logger.debug("请为sysFile设置分类编码和所属对象ID！");
            return null;
        }
        if (!file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String extendName = "."+ FileUtils.getExtension(file);
//            String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
            long size = file.getSize();
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
            //删除以前数据
            Map<String, Object> map=new HashMap<>();
            map.put("category_code",sysFile.getCategoryCode());
            map.put("object_id",sysFile.getObjectId());
            Integer del = fileMapper.deleteByMap(map);
            fileMapper.insert(sysFile);
        }
        return sysFile;
    }

    @Override
    public SysFile saveOnly(SysFile sysFile, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extendName = originalFilename.substring(originalFilename.lastIndexOf("."));
        long size = file.getSize();
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
        return sysFile;
    }

}
