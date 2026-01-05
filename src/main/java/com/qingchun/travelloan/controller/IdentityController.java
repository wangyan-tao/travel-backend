package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.UserIdentity;
import com.qingchun.travelloan.service.IdentityService;
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

@Tag(name = "实名信息管理")
@RestController
@RequestMapping("/identity")
public class IdentityController {

    @Autowired
    private IdentityService identityService;

    @Operation(summary = "提交实名信息")
    @PostMapping("/submit")
    public Result<UserIdentity> submit(@RequestBody UserIdentity request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserIdentity identity = identityService.submitIdentity(request, userId);
        return Result.success("实名信息提交成功", identity);
    }

    @Operation(summary = "获取实名信息")
    @GetMapping("/info")
    public Result<UserIdentity> info(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserIdentity identity = identityService.getIdentity(userId);
        return Result.success(identity);
    }
}

