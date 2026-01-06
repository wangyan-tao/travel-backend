package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.service.LoanProductService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员贷款产品管理控制器
 * 
 * @author Qingchun Team
 */
@Tag(name = "管理员-贷款产品管理")
@RestController
@RequestMapping("/admin/loan-products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanProductController {

    @Autowired
    private LoanProductService loanProductService;

    @Operation(summary = "获取所有贷款产品列表")
    @GetMapping
    public Result<com.qingchun.travelloan.vo.PageResult<LoanProduct>> getAllProducts(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        com.qingchun.travelloan.vo.PageResult<LoanProduct> pageResult = loanProductService.getAllProducts(page, size);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "获取产品ID列表（用于快速切换）")
    @GetMapping("/ids")
    public Result<List<Long>> getProductIds() {
        List<Long> ids = loanProductService.getAllProductIds();
        return Result.success(ids);
    }

    @Operation(summary = "根据ID获取产品详情")
    @GetMapping("/{id}")
    public Result<LoanProduct> getProduct(@PathVariable Long id) {
        LoanProduct product = loanProductService.getProductById(id, null);
        return Result.success(product);
    }

    @Operation(summary = "创建贷款产品")
    @PostMapping
    public Result<LoanProduct> createProduct(@RequestBody LoanProduct product) {
        LoanProduct created = loanProductService.createProduct(product);
        return Result.success("产品创建成功", created);
    }

    @Operation(summary = "更新贷款产品")
    @PutMapping("/{id}")
    public Result<LoanProduct> updateProduct(@PathVariable Long id, @RequestBody LoanProduct product) {
        LoanProduct updated = loanProductService.updateProduct(id, product);
        return Result.success("产品更新成功", updated);
    }

    @Operation(summary = "上架产品")
    @PutMapping("/{id}/activate")
    public Result<Void> activateProduct(@PathVariable Long id) {
        loanProductService.activateProduct(id);
        return Result.success("产品已上架");
    }

    @Operation(summary = "下架产品")
    @PutMapping("/{id}/deactivate")
    public Result<Void> deactivateProduct(@PathVariable Long id) {
        loanProductService.deactivateProduct(id);
        return Result.success("产品已下架");
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        loanProductService.deleteProduct(id);
        return Result.success("产品已删除");
    }
}

