package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.utils.FastDFSClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author chaoyue
 * @data2022-01-28 16:04
 */
@RestController
@RequestMapping("/file")
public class FileController {

    /***
     * 文件上传
     * @return
     */
    @PostMapping(value = "/upload")
    public Result upload(@RequestParam("file")MultipartFile file){
        try {
            //判断文件是否存在
            if (file == null){
                throw new RuntimeException("文件不存在");
            }
            //获取文件的完整名称
            String originalFilename = file.getOriginalFilename();
            if (org.apache.commons.lang.StringUtils.isEmpty(originalFilename)){
                throw new RuntimeException("文件不存在");
            }
            //封装一个FastDFSFile
            FastDFSFile fastDFSFile = new FastDFSFile(
                    file.getOriginalFilename(), //文件名字
                    file.getBytes(),            //文件字节数组
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));//文件扩展名

            //文件上传
            String[] uploads = FastDFSClient.upload(fastDFSFile);
            //组装文件上传地址
            String url = FastDFSClient.getTrackerUrl()+"/"+uploads[0]+"/"+uploads[1];

            return new Result(true,StatusCode.OK,"文件上传成功",url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(false, StatusCode.ERROR,"文件上传失败");
    }
}







