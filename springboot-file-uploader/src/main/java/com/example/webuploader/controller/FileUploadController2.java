package com.example.webuploader.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.webuploader.helper.FileUploadHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author booty
 * @date 2021/5/14 13:51
 */
@RestController
@Slf4j
@RequestMapping("maxFileUpload")
public class FileUploadController2 {

    private final FileUploadHelper fileUploadHelper = new FileUploadHelper("D:\\temp");

    /**
     * 验证文件是否存在【用于秒传】
     * @param verifyFileExistsJson
     * @return
     * @throws Exception
     */
    @RequestMapping("/verifyFileExists")
    public JSONObject verifyFileExists(@RequestBody JSONObject verifyFileExistsJson) throws Exception {
        boolean b = fileUploadHelper.verifyFileExists(
                verifyFileExistsJson.getString("fileMD5"),
                verifyFileExistsJson.getString("fileExt"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("exists", b);
        return jsonObject;

    }

    /**
     * 获取已上传的分片数量【用于初始化上传进度】
     * @param chunkUploadCountJson
     * @return
     * @throws Exception
     */
    @RequestMapping("/getChunkUploadCount")
    public JSONObject getChunkUploadCount(@RequestBody JSONObject chunkUploadCountJson) throws Exception {

        log.info("初始化上传进度getChunkUploadCount:{}" + chunkUploadCountJson.toString());

        int count = fileUploadHelper.getChunkUploadCount(chunkUploadCountJson.getString("fileMD5"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chunkCount", count);
        return jsonObject;

    }

    /**
     * 获取已上传的分片下标【用于分片上传前验证】
     * @param chunkUploadIndexJson
     * @return
     * @throws Exception
     */
    @RequestMapping("/getChunkUploadIndex")
    public JSONObject getChunkUploadIndex(@RequestBody JSONObject chunkUploadIndexJson) throws Exception {

        log.info("验证分片是否已经上传getChunkUploadIndex:{}" + chunkUploadIndexJson.toString());

        List<Integer> indexList = fileUploadHelper.getChunkUploadIndex(
                chunkUploadIndexJson.getString("fileMD5"),
                Integer.parseInt(chunkUploadIndexJson.getString("chunkSize")));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chunkIndex", indexList);
        return jsonObject;

    }

    /**
     * 实时验证分片是否已经上传【不推荐】
     * @throws Exception
     */
    @RequestMapping("/verifyChunk")
    public JSONObject verifyChunk(@RequestBody JSONObject verifyChunkJson) throws Exception {

        log.info("实时验证分片是否已经上传verifyChunk:{}" + verifyChunkJson.toString());

        boolean exists = fileUploadHelper.verifyChunk(verifyChunkJson.getString("fileMD5"),
                verifyChunkJson.getString("chunk"),
                Integer.parseInt(verifyChunkJson.getString("chunkSize")));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", exists);
        return jsonObject;

    }

    /**
     * 分片上传
     * @param file      上传的分片
     * @param fileMD5   上传文件的MD5值
     * @param chunk     分片下标
     * @param chunkSize 分片大小
     * @throws Exception
     */
    @RequestMapping("/upload")
    public JSONObject upload(@RequestParam("file") MultipartFile file, @RequestParam("fileMD5") String fileMD5,
                             @RequestParam("chunk") String chunk, @RequestParam("chunkSize") String chunkSize) throws Exception {

        log.info("分片上传upload,fileMD5:{};chunk:{};chunkSize:{}", fileMD5, chunk, chunkSize);

        boolean success;
        try (InputStream inputStream = file.getInputStream()) {
            success = fileUploadHelper.uploadChunk(inputStream, fileMD5, chunk);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        return jsonObject;

    }

    /**
     * 合并分片
     * @throws Exception
     */
    @RequestMapping("/marginFile")
    public JSONObject marginFile(@RequestBody JSONObject verifyChunkJson) throws Exception {

        log.info("文件后缀:{};文件MD5值:{};", verifyChunkJson.getString("fileExt"), verifyChunkJson.getString("fileMD5"));

        boolean success = fileUploadHelper.mergeFile(
                verifyChunkJson.getString("fileMD5"),
                verifyChunkJson.getString("fileExt"),
                Integer.parseInt(verifyChunkJson.getString("chunkCount")));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        return jsonObject;
    }

    /**
     * 取消文件上传
     * @param cancelFileJson
     * @return
     * @throws Exception
     */
    @RequestMapping("/cancel")
    public JSONObject cancel(@RequestBody JSONObject cancelFileJson) throws Exception {

        log.info("取消文件上传cancel:{}", cancelFileJson.toString());

        boolean success = fileUploadHelper.cancel(cancelFileJson.getString("fileMD5"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        return jsonObject;

    }






}
