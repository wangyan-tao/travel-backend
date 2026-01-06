package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.PartTimeJob;
import com.qingchun.travelloan.entity.UserJobProof;
import com.qingchun.travelloan.entity.UserLocation;
import com.qingchun.travelloan.entity.UserCertificate;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.PartTimeJobMapper;
import com.qingchun.travelloan.mapper.UserJobProofMapper;
import com.qingchun.travelloan.mapper.UserLocationMapper;
import com.qingchun.travelloan.mapper.UserCertificateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 兼职推荐服务
 * 
 * @author Qingchun Team
 */
@Slf4j
@Service
public class JobRecommendationService {

    @Autowired
    private PartTimeJobMapper partTimeJobMapper;

    @Autowired
    private UserJobProofMapper userJobProofMapper;

    @Autowired
    private UserLocationMapper userLocationMapper;

    @Autowired
    private UserCertificateMapper userCertificateMapper;

    /**
     * 获取兼职推荐列表
     * 强制使用用户认证时保存的城市信息，只显示和用户认证城市相同的兼职
     */
    public List<PartTimeJob> getRecommendations(Long userId, String status) {
        // 强制从用户位置信息获取城市
        UserLocation location = userLocationMapper.selectByUserId(userId);
        String userCity = null;
        if (location != null) {
            userCity = location.getCurrentCity() != null ? location.getCurrentCity() : location.getSchoolCity();
        }

        // 如果用户没有设置城市，返回空列表
        if (userCity == null || userCity.trim().isEmpty()) {
            log.warn("用户 {} 未设置城市信息，无法获取兼职推荐", userId);
            return java.util.Collections.emptyList();
        }

        // 构建查询条件
        QueryWrapper<PartTimeJob> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status != null ? status : "ACTIVE");
        
        // 强制使用用户认证时保存的城市
        wrapper.eq("city", userCity);
        
        wrapper.orderByDesc("created_at");
        
        List<PartTimeJob> jobs = partTimeJobMapper.selectList(wrapper);
        return jobs != null ? jobs : java.util.Collections.emptyList();
    }

    /**
     * 上传工作证明
     */
    @Transactional
    public UserJobProof uploadJobProof(Long userId, Map<String, Object> request) {
        log.info("上传工作证明 - userId: {}, request: {}", userId, request);
        
        UserJobProof proof = new UserJobProof();
        proof.setUserId(userId);
        
        // jobId 可选
        if (request.get("jobId") != null && !request.get("jobId").toString().isEmpty()) {
            try {
                proof.setJobId(Long.valueOf(request.get("jobId").toString()));
            } catch (NumberFormatException e) {
                log.warn("无效的jobId: {}", request.get("jobId"));
            }
        }
        
        // proofType 必填
        if (request.get("proofType") == null || request.get("proofType").toString().isEmpty()) {
            throw new BusinessException("证明类型不能为空");
        }
        proof.setProofType(request.get("proofType").toString());
        
        // proofUrl 必填
        if (request.get("proofUrl") == null || request.get("proofUrl").toString().isEmpty()) {
            throw new BusinessException("证明材料URL不能为空");
        }
        proof.setProofUrl(request.get("proofUrl").toString());
        
        // monthlyIncome 可选
        if (request.get("monthlyIncome") != null && !request.get("monthlyIncome").toString().isEmpty()) {
            try {
                proof.setMonthlyIncome(new BigDecimal(request.get("monthlyIncome").toString()));
            } catch (NumberFormatException e) {
                log.warn("无效的月收入: {}", request.get("monthlyIncome"));
            }
        }
        
        // startDate 必填
        if (request.get("startDate") == null || request.get("startDate").toString().isEmpty()) {
            throw new BusinessException("开始日期不能为空");
        }
        try {
            proof.setStartDate(LocalDate.parse(request.get("startDate").toString()));
        } catch (Exception e) {
            log.error("开始日期解析失败: {}", request.get("startDate"), e);
            throw new BusinessException("开始日期格式错误");
        }
        
        // endDate 可选
        if (request.get("endDate") != null && !request.get("endDate").toString().isEmpty()) {
            try {
                proof.setEndDate(LocalDate.parse(request.get("endDate").toString()));
            } catch (Exception e) {
                log.warn("无效的结束日期: {}", request.get("endDate"));
            }
        }
        
        proof.setVerificationStatus("PENDING");
        
        log.info("保存工作证明 - proof: {}", proof);
        userJobProofMapper.insert(proof);
        log.info("工作证明保存成功 - id: {}", proof.getId());
        
        // 创建user_certificate记录
        createUserCertificate(userId, "JOB_PROOF", proof.getId(), proof.getProofType(), proof.getProofUrl(), 
                buildJobProofDescription(proof));
        
        return proof;
    }

    /**
     * 获取用户工作证明列表（包含关联的兼职信息）
     * 从user_certificate表查询状态并映射到verificationStatus
     */
    public List<UserJobProof> getUserJobProofs(Long userId) {
        log.info("获取用户工作证明列表 - userId: {}", userId);
        List<UserJobProof> proofs = userJobProofMapper.selectByUserIdWithJobInfo(userId);
        log.info("查询到工作证明数量: {}", proofs != null ? proofs.size() : 0);
        
        if (proofs == null || proofs.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        // 从user_certificate表查询状态并映射
        for (UserJobProof proof : proofs) {
            QueryWrapper<UserCertificate> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.eq("certificate_type", "JOB_PROOF");
            wrapper.eq("source_id", proof.getId());
            UserCertificate certificate = userCertificateMapper.selectOne(wrapper);
            
            if (certificate != null) {
                // 将user_certificate.status映射到verificationStatus
                String certificateStatus = certificate.getStatus();
                if ("APPROVED".equals(certificateStatus)) {
                    proof.setVerificationStatus("VERIFIED");
                } else if ("REJECTED".equals(certificateStatus)) {
                    proof.setVerificationStatus("FAILED");
                } else {
                    proof.setVerificationStatus("PENDING");
                }
            } else {
                // 如果没有找到证书记录，默认为待核验
                proof.setVerificationStatus("PENDING");
            }
        }
        
        return proofs;
    }

    /**
     * 删除工作证明
     */
    @Transactional
    public void deleteJobProof(Long userId, Long proofId) {
        UserJobProof proof = userJobProofMapper.selectById(proofId);
        if (proof == null) {
            throw new BusinessException("工作证明不存在");
        }
        if (!proof.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此工作证明");
        }
        userJobProofMapper.deleteById(proofId);
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
     * 构建工作证明描述
     */
    private String buildJobProofDescription(UserJobProof proof) {
        StringBuilder desc = new StringBuilder();
        desc.append("证明类型：").append(proof.getProofType());
        if (proof.getMonthlyIncome() != null) {
            desc.append("，月收入：¥").append(proof.getMonthlyIncome());
        }
        desc.append("，工作期间：").append(proof.getStartDate());
        if (proof.getEndDate() != null) {
            desc.append(" 至 ").append(proof.getEndDate());
        }
        return desc.toString();
    }
}

