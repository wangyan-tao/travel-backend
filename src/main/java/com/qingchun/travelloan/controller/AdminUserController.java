package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.service.AdminUserService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员用户管理控制器
 *
 * @author Qingchun Team
 */
@Tag(name = "管理员-用户管理")
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Operation(summary = "获取用户列表")
    @GetMapping
    public Result<com.qingchun.travelloan.vo.PageResult<User>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        com.qingchun.travelloan.vo.PageResult<User> pageResult = adminUserService.getAllUsers(keyword, role, page, size);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "获取用户ID列表（用于快速切换）")
    @GetMapping("/ids")
    public Result<List<Long>> getUserIds(
            @RequestParam(required = false) String role) {
        List<Long> ids = adminUserService.getAllUserIds(role);
        return Result.success(ids);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = adminUserService.getUserById(id);
        return Result.success(user);
    }

    @Operation(summary = "启用用户")
    @PutMapping("/{id}/enable")
    public Result<Void> enableUser(@PathVariable Long id) {
        adminUserService.enableUser(id);
        return Result.success("用户已启用");
    }

    @Operation(summary = "禁用用户")
    @PutMapping("/{id}/disable")
    public Result<Void> disableUser(@PathVariable Long id) {
        adminUserService.disableUser(id);
        return Result.success("用户已禁用");
    }
}

