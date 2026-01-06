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
        identity.setStudentId(request.getStudentId());
        identity.setStudentCardUrl(request.getStudentCardUrl());
        identity.setUniversity(request.getUniversity());
        identity.setMajor(request.getMajor());
        identity.setGrade(request.getGrade());

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

        try {
            UserLocation location = userLocationMapper.selectByUserId(userId);
            if (location == null) {
                log.info("创建新的用户位置记录 - userId: {}, city: {}", userId, city);
                location = new UserLocation();
                location.setUserId(userId);
                location.setCurrentCity(city);
                userLocationMapper.insert(location);
                log.info("用户位置记录创建成功 - userId: {}, city: {}", userId, city);
            } else {
                log.info("更新用户位置记录 - userId: {}, oldCity: {}, newCity: {}", userId, location.getCurrentCity(), city);
                location.setCurrentCity(city);
                userLocationMapper.updateById(location);
                log.info("用户位置记录更新成功 - userId: {}, city: {}", userId, city);
            }
        } catch (Exception e) {
            log.error("保存用户城市信息失败 - userId: {}, city: {}", userId, city, e);
            throw new BusinessException("保存城市信息失败: " + e.getMessage());
        }
    }

    public UserIdentity getIdentity(Long userId) {
        return userIdentityMapper.selectOne(
                new QueryWrapper<UserIdentity>().eq("user_id", userId)
        );
    }

    private void validateRequest(UserIdentity request) {
        if (isBlank(request.getRealName())
                || isBlank(request.getIdCard())
                || isBlank(request.getStudentId())
                || isBlank(request.getUniversity())
                || isBlank(request.getMajor())
                || isBlank(request.getGrade())
        ) {
            throw new BusinessException("请完整填写必填信息");
        }
        if (isBlank(request.getIdCardFrontUrl()) || isBlank(request.getIdCardBackUrl()) || isBlank(request.getStudentCardUrl())) {
            throw new BusinessException("请上传身份证正反面和学生证照片");
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
