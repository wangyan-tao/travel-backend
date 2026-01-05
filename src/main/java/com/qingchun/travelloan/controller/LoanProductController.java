package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.service.LoanProductService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "贷款产品管理")
@RestController
@RequestMapping("/loan-products")
public class LoanProductController {

    @Autowired
    private LoanProductService loanProductService;

    @Operation(summary = "获取贷款产品列表")
    @GetMapping
    public Result<List<LoanProduct>> listLoanProducts(
            @RequestParam(required = false, name = "productType") String productType,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "6") Integer size) {
        List<LoanProduct> products = loanProductService.listLoanProducts(productType, page, size);
        return Result.success(products);
    }
}
