package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.AcademicHonor;
import com.qingchun.travelloan.service.AcademicHonorService;
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
 * 学业荣誉控制器
 * 
 * @author Qingchun Team
 */
@Tag(name = "学业荣誉管理", description = "学业荣誉证明相关接口")
@RestController
@RequestMapping("/academic-honors")
public class AcademicHonorController {

    @Autowired
    private AcademicHonorService academicHonorService;

    /**
     * 上传学业荣誉证明
     */
    @Operation(summary = "上传学业荣誉证明")
    @PostMapping
    public Result<AcademicHonor> uploadHonor(
            @Valid @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        AcademicHonor honor = academicHonorService.uploadHonor(userId, request);
        return Result.success("学业荣誉上传成功", honor);
    }

    /**
     * 获取用户学业荣誉列表
     */
    @Operation(summary = "获取用户学业荣誉列表")
    @GetMapping
    public Result<List<AcademicHonor>> getUserHonors(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<AcademicHonor> honors = academicHonorService.getUserHonors(userId);
        return Result.success(honors);
    }

    /**
     * 删除学业荣誉
     */
    @Operation(summary = "删除学业荣誉")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHonor(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        academicHonorService.deleteHonor(userId, id);
        return Result.success();
    }
}

