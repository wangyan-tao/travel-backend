package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.AcademicHonor;
import com.qingchun.travelloan.entity.UserCertificate;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.AcademicHonorMapper;
import com.qingchun.travelloan.mapper.UserCertificateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 学业荣誉服务
 * 
 * @author Qingchun Team
 */
@Slf4j
@Service
public class AcademicHonorService {

    @Autowired
    private AcademicHonorMapper academicHonorMapper;

    @Autowired
    private UserCertificateMapper userCertificateMapper;

    /**
     * 上传学业荣誉证明
     */
    @Transactional
    public AcademicHonor uploadHonor(Long userId, Map<String, Object> request) {
        log.info("上传学业荣誉 - userId: {}, request: {}", userId, request);
        
        AcademicHonor honor = new AcademicHonor();
        honor.setUserId(userId);
        
        // honorType 必填
        if (request.get("honorType") == null || request.get("honorType").toString().isEmpty()) {
            throw new BusinessException("荣誉类型不能为空");
        }
        honor.setHonorType(request.get("honorType").toString());
        
        // honorName 必填
        if (request.get("honorName") == null || request.get("honorName").toString().isEmpty()) {
            throw new BusinessException("荣誉名称不能为空");
        }
        honor.setHonorName(request.get("honorName").toString());
        
        // awardLevel 可选
        if (request.get("awardLevel") != null && !request.get("awardLevel").toString().isEmpty()) {
            honor.setAwardLevel(request.get("awardLevel").toString());
        }
        
        // awardDate 可选
        if (request.get("awardDate") != null && !request.get("awardDate").toString().isEmpty()) {
            try {
                honor.setAwardDate(LocalDate.parse(request.get("awardDate").toString()));
            } catch (Exception e) {
                log.warn("无效的获奖日期: {}", request.get("awardDate"));
            }
        }
        
        // certificateUrl 必填
        if (request.get("certificateUrl") == null || request.get("certificateUrl").toString().isEmpty()) {
            throw new BusinessException("证书URL不能为空");
        }
        honor.setCertificateUrl(request.get("certificateUrl").toString());
        
        // issuingOrganization 可选
        if (request.get("issuingOrganization") != null && !request.get("issuingOrganization").toString().isEmpty()) {
            honor.setIssuingOrganization(request.get("issuingOrganization").toString());
        }
        
        honor.setVerificationStatus("PENDING");
        
        log.info("保存学业荣誉 - honor: {}", honor);
        academicHonorMapper.insert(honor);
        log.info("学业荣誉保存成功 - id: {}", honor.getId());
        
        // 创建user_certificate记录
        createUserCertificate(userId, "ACADEMIC_HONOR", honor.getId(), honor.getHonorName(), 
                honor.getCertificateUrl(), buildHonorDescription(honor));
        
        return honor;
    }

    /**
     * 获取用户学业荣誉列表
     * 从user_certificate表查询状态并映射到verificationStatus
     */
    public List<AcademicHonor> getUserHonors(Long userId) {
        log.info("获取用户学业荣誉列表 - userId: {}", userId);
        List<AcademicHonor> honors = academicHonorMapper.selectByUserId(userId);
        log.info("查询到学业荣誉数量: {}", honors != null ? honors.size() : 0);
        
        if (honors == null || honors.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        // 从user_certificate表查询状态并映射
        for (AcademicHonor honor : honors) {
            QueryWrapper<UserCertificate> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.eq("certificate_type", "ACADEMIC_HONOR");
            wrapper.eq("source_id", honor.getId());
            UserCertificate certificate = userCertificateMapper.selectOne(wrapper);
            
            if (certificate != null) {
                // 将user_certificate.status映射到verificationStatus
                String certificateStatus = certificate.getStatus();
                if ("APPROVED".equals(certificateStatus)) {
                    honor.setVerificationStatus("VERIFIED");
                } else if ("REJECTED".equals(certificateStatus)) {
                    honor.setVerificationStatus("FAILED");
                } else {
                    honor.setVerificationStatus("PENDING");
                }
            } else {
                // 如果没有找到证书记录，默认为待核验
                honor.setVerificationStatus("PENDING");
            }
        }
        
        return honors;
    }

    /**
     * 删除学业荣誉
     */
    @Transactional
    public void deleteHonor(Long userId, Long honorId) {
        AcademicHonor honor = academicHonorMapper.selectById(honorId);
        if (honor == null) {
            throw new BusinessException("学业荣誉不存在");
        }
        if (!honor.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此学业荣誉");
        }
        academicHonorMapper.deleteById(honorId);
    }

    /**
     * 创建user_certificate记录
     */
    private void createUserCertificate(Long userId, String certificateType, Long sourceId, 
                                       String certificateName, String certificateUrl, String description) {
        UserCertificate certificate = new UserCertificate();
        certificate.setUserId(userId);
        certificate.setCertificateType(certificateType);
        certificate.setSourceId(sourceId);
        certificate.setCertificateName(certificateName);
        certificate.setCertificateUrl(certificateUrl);
        certificate.setDescription(description);
        certificate.setStatus("PENDING");
        
        userCertificateMapper.insert(certificate);
        log.info("创建user_certificate记录成功 - id: {}, type: {}, sourceId: {}", 
                certificate.getId(), certificateType, sourceId);
    }

    /**
     * 构建学业荣誉描述
     */
    private String buildHonorDescription(AcademicHonor honor) {
        StringBuilder desc = new StringBuilder();
        desc.append("荣誉类型：").append(honor.getHonorType());
        if (honor.getAwardLevel() != null) {
            desc.append("，获奖级别：").append(honor.getAwardLevel());
        }
        if (honor.getAwardDate() != null) {
            desc.append("，获奖日期：").append(honor.getAwardDate());
        }
        if (honor.getIssuingOrganization() != null) {
            desc.append("，颁发机构：").append(honor.getIssuingOrganization());
        }
        return desc.toString();
    }
}

