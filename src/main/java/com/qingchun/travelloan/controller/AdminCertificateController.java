package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.service.AdminCertificateService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员证书审批控制器
 *
 * @author Qingchun Team
 */
@Tag(name = "管理员-证书审批管理")
@RestController
@RequestMapping("/admin/certificates")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCertificateController {

    @Autowired
    private AdminCertificateService adminCertificateService;

    @Operation(summary = "获取证书列表")
    @GetMapping
    public Result<com.qingchun.travelloan.vo.PageResult<AdminCertificateService.CertificateDTO>> getCertificates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        com.qingchun.travelloan.vo.PageResult<AdminCertificateService.CertificateDTO> pageResult =
                adminCertificateService.getCertificates(keyword, status, page, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取证书详情")
    @GetMapping("/{id}")
    public Result<AdminCertificateService.CertificateDTO> getCertificate(@PathVariable Long id) {
        AdminCertificateService.CertificateDTO certificate = adminCertificateService.getCertificateById(id);
        return Result.success(certificate);
    }

    @Operation(summary = "审批通过")
    @PostMapping("/{id}/approve")
    public Result<Void> approveCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long approverId = (Long) authentication.getPrincipal();
        String approvalOpinion = request.get("approvalOpinion");
        adminCertificateService.approveCertificate(id, approverId, approvalOpinion);
        return Result.success("证书已通过审批");
    }

    @Operation(summary = "审批拒绝")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long approverId = (Long) authentication.getPrincipal();
        String approvalOpinion = request.get("approvalOpinion");
        adminCertificateService.rejectCertificate(id, approverId, approvalOpinion);
        return Result.success("证书已拒绝");
    }
}

