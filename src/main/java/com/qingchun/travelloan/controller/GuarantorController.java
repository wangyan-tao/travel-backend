package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.Guarantor;
import com.qingchun.travelloan.service.GuarantorService;
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

@Tag(name = "担保人信息")
@RestController
@RequestMapping("/guarantor")
public class GuarantorController {

    @Autowired
    private GuarantorService guarantorService;

    @Operation(summary = "提交担保人信息")
    @PostMapping("/submit")
    public Result<Guarantor> submit(@RequestBody Guarantor request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Guarantor g = guarantorService.submit(request, userId);
        return Result.success("担保人信息提交成功", g);
    }

    @Operation(summary = "获取担保人信息")
    @GetMapping("/info")
    public Result<Guarantor> info(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Guarantor g = guarantorService.info(userId);
        return Result.success(g);
    }
}
