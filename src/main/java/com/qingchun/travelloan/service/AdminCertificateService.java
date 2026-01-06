package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingchun.travelloan.entity.UserCertificate;
import com.qingchun.travelloan.entity.UserJobProof;
import com.qingchun.travelloan.entity.AcademicHonor;
import com.qingchun.travelloan.mapper.UserCertificateMapper;
import com.qingchun.travelloan.mapper.UserJobProofMapper;
import com.qingchun.travelloan.mapper.AcademicHonorMapper;
import com.qingchun.travelloan.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员证书审批服务
 *
 * @author Qingchun Team
 */
@Slf4j
@Service
public class AdminCertificateService {

    @Autowired
    private UserCertificateMapper userCertificateMapper;

    @Autowired
    private UserJobProofMapper userJobProofMapper;

    @Autowired
    private AcademicHonorMapper academicHonorMapper;

    /**
     * 证书DTO
     */
    public static class CertificateDTO {
        public Long id;
        public Long userId;
        public String username;
        public String certificateType;
        public String certificateTypeName;
        public String certificateName;
        public String certificateUrl;
        public String description;
        public String status;
        public String statusName;
        public Long approverId;
        public String approvalOpinion;
        public LocalDateTime approvedAt;
        public LocalDateTime createdAt;
        public LocalDateTime updatedAt;
    }

    /**
     * 获取证书列表（分页）
     */
    public com.qingchun.travelloan.vo.PageResult<CertificateDTO> getCertificates(
            String keyword, String status, Integer page, Integer size) {
        Page<UserCertificate> pageParam = new Page<>(page, size);
        QueryWrapper<UserCertificate> wrapper = new QueryWrapper<>();

        if (status != null && !status.isEmpty() && !"ALL".equals(status)) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("created_at");

        Page<UserCertificate> pageResult = userCertificateMapper.selectPage(pageParam, wrapper);
        List<CertificateDTO> dtos = pageResult.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new com.qingchun.travelloan.vo.PageResult<CertificateDTO>(
                dtos,
                pageResult.getTotal(),
                (int) pageResult.getCurrent(),
                (int) pageResult.getSize()
        );
    }

    /**
     * 获取证书详情
     */
    public CertificateDTO getCertificateById(Long id) {
        UserCertificate certificate = userCertificateMapper.selectById(id);
        if (certificate == null) {
            throw new BusinessException("证书不存在");
        }
        return convertToDTO(certificate);
    }

    /**
     * 审批通过
     */
    @Transactional
    public void approveCertificate(Long id, Long approverId, String approvalOpinion) {
        UserCertificate certificate = userCertificateMapper.selectById(id);
        if (certificate == null) {
            throw new BusinessException("证书不存在");
        }
        if (!"PENDING".equals(certificate.getStatus())) {
            throw new BusinessException("证书状态不正确，无法审批");
        }

        certificate.setStatus("APPROVED");
        certificate.setApproverId(approverId);
        certificate.setApprovalOpinion(approvalOpinion);
        certificate.setApprovedAt(LocalDateTime.now());

        userCertificateMapper.updateById(certificate);

        // 同步更新原始表的verification_status
        syncVerificationStatus(certificate, "VERIFIED");
    }

    /**
     * 审批拒绝
     */
    @Transactional
    public void rejectCertificate(Long id, Long approverId, String approvalOpinion) {
        UserCertificate certificate = userCertificateMapper.selectById(id);
        if (certificate == null) {
            throw new BusinessException("证书不存在");
        }
        if (!"PENDING".equals(certificate.getStatus())) {
            throw new BusinessException("证书状态不正确，无法审批");
        }

        certificate.setStatus("REJECTED");
        certificate.setApproverId(approverId);
        certificate.setApprovalOpinion(approvalOpinion);
        certificate.setApprovedAt(LocalDateTime.now());

        userCertificateMapper.updateById(certificate);

        // 同步更新原始表的verification_status
        syncVerificationStatus(certificate, "FAILED");
    }

    /**
     * 同步更新原始表的verification_status
     */
    private void syncVerificationStatus(UserCertificate certificate, String status) {
        if ("JOB_PROOF".equals(certificate.getCertificateType())) {
            UserJobProof proof = userJobProofMapper.selectById(certificate.getSourceId());
            if (proof != null) {
                proof.setVerificationStatus(status);
                userJobProofMapper.updateById(proof);
            }
        } else if ("ACADEMIC_HONOR".equals(certificate.getCertificateType())) {
            AcademicHonor honor = academicHonorMapper.selectById(certificate.getSourceId());
            if (honor != null) {
                honor.setVerificationStatus(status);
                academicHonorMapper.updateById(honor);
            }
        }
    }

    /**
     * 转换为DTO
     */
    private CertificateDTO convertToDTO(UserCertificate certificate) {
        CertificateDTO dto = new CertificateDTO();
        dto.id = certificate.getId();
        dto.userId = certificate.getUserId();
        dto.certificateType = certificate.getCertificateType();
        dto.certificateTypeName = "JOB_PROOF".equals(certificate.getCertificateType()) ? "工作证明" : "学业荣誉";
        dto.certificateName = certificate.getCertificateName();
        dto.certificateUrl = certificate.getCertificateUrl();
        dto.description = certificate.getDescription();
        dto.status = certificate.getStatus();
        dto.statusName = getStatusName(certificate.getStatus());
        dto.approverId = certificate.getApproverId();
        dto.approvalOpinion = certificate.getApprovalOpinion();
        dto.approvedAt = certificate.getApprovedAt();
        dto.createdAt = certificate.getCreatedAt();
        dto.updatedAt = certificate.getUpdatedAt();
        return dto;
    }

    private String getStatusName(String status) {
        switch (status) {
            case "PENDING":
                return "待审批";
            case "APPROVED":
                return "已通过";
            case "REJECTED":
                return "已拒绝";
            default:
                return status;
        }
    }
}

