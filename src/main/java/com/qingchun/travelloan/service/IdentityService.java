package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.UserIdentity;
import com.qingchun.travelloan.entity.UserLocation;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.UserIdentityMapper;
import com.qingchun.travelloan.mapper.UserLocationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class IdentityService {

    @Autowired
    private UserIdentityMapper userIdentityMapper;

    @Autowired
    private UserLocationMapper userLocationMapper;

    @Transactional
    public UserIdentity submitIdentity(UserIdentity request, Long userId) {
        if (request == null) {
            throw new BusinessException("实名信息不能为空");
        }
        validateRequest(request);
        // 图片已改为上传到七牛云，前端提交的是URL，无需验证Base64大小
        // validateImageSize(request.getIdCardFrontUrl(), "身份证正面");
        // validateImageSize(request.getIdCardBackUrl(), "身份证反面");
        // validateImageSize(request.getStudentCardUrl(), "学生证");

        UserIdentity identity = userIdentityMapper.selectOne(
                new QueryWrapper<UserIdentity>().eq("user_id", userId)
        );
        if (identity == null) {
            identity = new UserIdentity();
            identity.setUserId(userId);
        }

        identity.setRealName(request.getRealName());
        identity.setIdCard(request.getIdCard());
        identity.setIdCardFrontUrl(request.getIdCardFrontUrl());
        identity.setIdCardBackUrl(request.getIdCardBackUrl());
        identity.setUniversity(request.getUniversity());
        // 以下字段已移除：studentId, studentCardUrl, major, grade
        // 设置为默认值，避免数据库 NOT NULL 约束错误
        identity.setStudentId("");
        identity.setStudentCardUrl("");
        identity.setMajor("");
        identity.setGrade("");

        identity.setVerificationStatus("VERIFIED");
        identity.setVerifiedAt(LocalDateTime.now());

        if (identity.getId() == null) {
            userIdentityMapper.insert(identity);
        } else {
            userIdentityMapper.updateById(identity);
        }

        // 保存城市信息到user_location表
        log.info("提交实名认证 - userId: {}, city: {}", userId, request.getCity());
        saveUserLocation(userId, request.getCity());

        return identity;
    }

    /**
     * 保存用户城市信息到user_location表
     */
    private void saveUserLocation(Long userId, String city) {
        log.info("保存用户城市信息 - userId: {}, city: {}", userId, city);
        if (city == null || city.trim().isEmpty()) {
            log.warn("城市信息为空，跳过保存 - userId: {}", userId);
            return;
        }

        // 提取城市名称：如果格式是"省份-城市"，则只取城市部分
        String cityName = extractCityName(city);
        log.info("提取后的城市名称 - userId: {}, original: {}, extracted: {}", userId, city, cityName);

        try {
            UserLocation location = userLocationMapper.selectByUserId(userId);
            if (location == null) {
                log.info("创建新的用户位置记录 - userId: {}, city: {}", userId, cityName);
                location = new UserLocation();
                location.setUserId(userId);
                location.setCurrentCity(cityName);
                userLocationMapper.insert(location);
                log.info("用户位置记录创建成功 - userId: {}, city: {}", userId, cityName);
            } else {
                log.info("更新用户位置记录 - userId: {}, oldCity: {}, newCity: {}", userId, location.getCurrentCity(), cityName);
                location.setCurrentCity(cityName);
                userLocationMapper.updateById(location);
                log.info("用户位置记录更新成功 - userId: {}, city: {}", userId, cityName);
            }
        } catch (Exception e) {
            log.error("保存用户城市信息失败 - userId: {}, city: {}", userId, cityName, e);
            throw new BusinessException("保存城市信息失败: " + e.getMessage());
        }
    }

    /**
     * 从城市字符串中提取城市名称
     * 如果格式是"省份-城市"，则返回城市部分；否则返回原字符串
     * 例如："四川省-宜宾市" -> "宜宾市"
     */
    private String extractCityName(String city) {
        if (city == null || city.trim().isEmpty()) {
            return city;
        }
        
        // 如果包含"-"分隔符，取最后一部分作为城市名称
        if (city.contains("-")) {
            String[] parts = city.split("-");
            if (parts.length > 0) {
                return parts[parts.length - 1].trim();
            }
        }
        
        // 对于直辖市（如"北京市"、"上海市"），直接返回
        // 如果已经是纯城市名称，直接返回
        return city.trim();
    }

    public UserIdentity getIdentity(Long userId) {
        return userIdentityMapper.selectOne(
                new QueryWrapper<UserIdentity>().eq("user_id", userId)
        );
    }

    private void validateRequest(UserIdentity request) {
        if (isBlank(request.getRealName())
                || isBlank(request.getIdCard())
                || isBlank(request.getUniversity())
        ) {
            throw new BusinessException("请完整填写必填信息");
        }
        if (isBlank(request.getIdCardFrontUrl()) || isBlank(request.getIdCardBackUrl())) {
            throw new BusinessException("请上传身份证正反面照片");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void validateImageSize(String base64, String name) {
        if (base64 == null) return;
        int max = 16 * 1024 * 1024;
        if (base64.length() > max) {
            throw new BusinessException(name + "图片过大");
        }
    }
}
