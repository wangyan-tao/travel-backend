package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.UserLocation;
import com.qingchun.travelloan.service.UserLocationService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户位置控制器
 * 
 * @author Qingchun Team
 */
@Tag(name = "用户位置管理", description = "用户位置信息相关接口")
@RestController
@RequestMapping("/user/location")
public class UserLocationController {

    @Autowired
    private UserLocationService userLocationService;

    /**
     * 获取用户位置信息
     */
    @Operation(summary = "获取用户位置信息")
    @GetMapping
    public Result<UserLocation> getUserLocation(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserLocation location = userLocationService.getUserLocation(userId);
        return Result.success(location);
    }

    /**
     * 更新用户位置信息
     */
    @Operation(summary = "更新用户位置信息")
    @PutMapping
    public Result<UserLocation> updateUserLocation(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserLocation location = userLocationService.updateUserLocation(userId, province, city);
        return Result.success(location);
    }
}

