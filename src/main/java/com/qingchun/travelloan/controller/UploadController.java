package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.service.OssService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "文件上传")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private OssService ossService;

    @Operation(summary = "上传文件")
    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = ossService.uploadFile(file);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success("上传成功", data);
    }
}
