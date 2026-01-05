package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.PartTimeJob;
import com.qingchun.travelloan.entity.UserJobProof;
import com.qingchun.travelloan.entity.UserLocation;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.PartTimeJobMapper;
import com.qingchun.travelloan.mapper.UserJobProofMapper;
import com.qingchun.travelloan.mapper.UserLocationMapper;
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

    /**
     * 获取兼职推荐列表
     * 优先使用用户位置信息，如果没有则使用传入的参数
     */
    public List<PartTimeJob> getRecommendations(Long userId, String city, String district, String jobType, String status) {
        // 如果未传入城市，尝试从用户位置信息获取
        if (city == null || city.isEmpty()) {
            UserLocation location = userLocationMapper.selectByUserId(userId);
            if (location != null) {
                city = location.getCurrentCity() != null ? location.getCurrentCity() : location.getSchoolCity();
            }
        }

        // 构建查询条件
        QueryWrapper<PartTimeJob> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status != null ? status : "ACTIVE");
        
        if (city != null && !city.isEmpty()) {
            wrapper.eq("city", city);
        }
        if (district != null && !district.isEmpty()) {
            wrapper.eq("district", district);
        }
        if (jobType != null && !jobType.isEmpty()) {
            wrapper.eq("job_type", jobType);
        }
        
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
        
        return proof;
    }

    /**
     * 获取用户工作证明列表（包含关联的兼职信息）
     */
    public List<UserJobProof> getUserJobProofs(Long userId) {
        log.info("获取用户工作证明列表 - userId: {}", userId);
        List<UserJobProof> proofs = userJobProofMapper.selectByUserIdWithJobInfo(userId);
        log.info("查询到工作证明数量: {}", proofs != null ? proofs.size() : 0);
        return proofs != null ? proofs : java.util.Collections.emptyList();
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
}

