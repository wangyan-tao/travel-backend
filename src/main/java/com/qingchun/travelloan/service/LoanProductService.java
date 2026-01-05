package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.mapper.LoanProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanProductService {

    @Autowired
    private LoanProductMapper loanProductMapper;

    public List<LoanProduct> listLoanProducts(String productType, Integer page, Integer size) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 6 : size;
        QueryWrapper<LoanProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "ACTIVE");
        if (productType != null && !productType.trim().isEmpty()) {
            wrapper.eq("product_type", productType.trim());
        }
        wrapper.orderByAsc("id");
        int offset = (currentPage - 1) * pageSize;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        return loanProductMapper.selectList(wrapper);
    }
}
