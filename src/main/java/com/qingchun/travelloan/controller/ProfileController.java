package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.dto.ProfileDTO;
import com.qingchun.travelloan.service.ProfileService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "个人信息")
@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @Operation(summary = "获取个人信息")
    @GetMapping("/me")
    public Result<ProfileDTO> me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        ProfileDTO data = profileService.getProfile(userId);
        return Result.success(data);
    }
}
