package com.qingchun.travelloan.service;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qingchun.travelloan.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class OssService {

    @Value("${qiniu.access-key}")
    private String accessKey;

    @Value("${qiniu.secret-key}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;

    /**
     * 上传文件到七牛云
     */
    public String uploadFile(MultipartFile file) {
        // 构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本

        UploadManager uploadManager = new UploadManager(cfg);

        // 生成上传凭证，然后准备上传
        String upToken = getUpToken();

        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String key = UUID.randomUUID().toString().replace("-", "") + suffix;

            Response response = uploadManager.put(file.getInputStream(), key, upToken, null, null);
            
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info("文件上传成功: key={}, hash={}", putRet.key, putRet.hash);
            
            String base = normalizeDomain(domain);
            return base + "/" + putRet.key;
        } catch (QiniuException ex) {
            log.error("七牛云上传异常: {}", ex.getMessage());
            throw new BusinessException("文件上传失败: " + ex.getMessage());
        } catch (IOException ex) {
            log.error("文件读取异常: {}", ex.getMessage());
            throw new BusinessException("文件上传失败: 文件读取错误");
        }
    }

    /**
     * 获取上传凭证
     */
    private String getUpToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }

    private String normalizeDomain(String d) {
        if (d == null || d.trim().isEmpty()) {
            throw new BusinessException("未配置对象存储域名");
        }
        String trimmed = d.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed.replaceAll("/+$", "");
        }
        return ("http://" + trimmed).replaceAll("/+$", "");
    }
}
