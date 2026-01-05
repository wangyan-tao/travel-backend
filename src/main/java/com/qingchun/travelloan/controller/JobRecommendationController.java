package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.PartTimeJob;
import com.qingchun.travelloan.entity.UserJobProof;
import com.qingchun.travelloan.service.JobRecommendationService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 兼职推荐控制器
 * 
 * @author Qingchun Team
 */
@Tag(name = "兼职推荐管理", description = "兼职推荐和工作证明相关接口")
@RestController
@RequestMapping("/jobs")
public class JobRecommendationController {

    @Autowired
    private JobRecommendationService jobRecommendationService;

    /**
     * 获取兼职推荐列表
     */
    @Operation(summary = "获取兼职推荐列表")
    @GetMapping("/recommendations")
    public Result<List<PartTimeJob>> getRecommendations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "ACTIVE") String status,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<PartTimeJob> jobs = jobRecommendationService.getRecommendations(userId, city, district, jobType, status);
        return Result.success(jobs);
    }

    /**
     * 上传工作证明
     */
    @Operation(summary = "上传工作证明")
    @PostMapping("/proof")
    public Result<UserJobProof> uploadJobProof(
            @Valid @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserJobProof proof = jobRecommendationService.uploadJobProof(userId, request);
        return Result.success("工作证明上传成功", proof);
    }

    /**
     * 获取用户工作证明列表
     */
    @Operation(summary = "获取用户工作证明列表")
    @GetMapping("/proof")
    public Result<List<UserJobProof>> getUserJobProofs(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<UserJobProof> proofs = jobRecommendationService.getUserJobProofs(userId);
        return Result.success(proofs);
    }

    /**
     * 删除工作证明
     */
    @Operation(summary = "删除工作证明")
    @DeleteMapping("/proof/{id}")
    public Result<Void> deleteJobProof(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        jobRecommendationService.deleteJobProof(userId, id);
        return Result.success();
    }
}

