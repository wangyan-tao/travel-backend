package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.UserIdentity;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.UserIdentityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IdentityService {

    @Autowired
    private UserIdentityMapper userIdentityMapper;

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

        identity.setVerificationStatus("VERIFIED");
        identity.setVerifiedAt(LocalDateTime.now());

        if (identity.getId() == null) {
            userIdentityMapper.insert(identity);
        } else {
            userIdentityMapper.updateById(identity);
        }
        return identity;
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
