package com.qingchun.travelloan.service;

import com.qingchun.travelloan.entity.AcademicHonor;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.AcademicHonorMapper;
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
        
        return honor;
    }

    /**
     * 获取用户学业荣誉列表
     */
    public List<AcademicHonor> getUserHonors(Long userId) {
        log.info("获取用户学业荣誉列表 - userId: {}", userId);
        List<AcademicHonor> honors = academicHonorMapper.selectByUserId(userId);
        log.info("查询到学业荣誉数量: {}", honors != null ? honors.size() : 0);
        return honors != null ? honors : java.util.Collections.emptyList();
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
}

