package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.dto.EvaluationRequest;
import com.qingchun.travelloan.entity.EvaluationQuestionnaire;
import com.qingchun.travelloan.service.EvaluationService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测评问卷控制器
 * 
 * @author Qingchun Team
 */
@Tag(name = "测评问卷管理")
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Operation(summary = "提交测评问卷")
    @PostMapping("/submit")
    public Result<EvaluationQuestionnaire> submit(@RequestBody EvaluationRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        EvaluationQuestionnaire evaluation = evaluationService.submitEvaluation(request, userId);
        return Result.success("测评提交成功", evaluation);
    }

    @Operation(summary = "获取测评结果")
    @GetMapping("/result")
    public Result<EvaluationQuestionnaire> getResult(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        EvaluationQuestionnaire evaluation = evaluationService.getEvaluation(userId);
        return Result.success(evaluation);
    }

    @Operation(summary = "检查是否完成测评")
    @GetMapping("/check")
    public Result<Boolean> check(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        boolean completed = evaluationService.hasCompletedEvaluation(userId);
        return Result.success(completed);
    }
}

