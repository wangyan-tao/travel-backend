package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.LoanProduct;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.LoanProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 贷款产品服务类
 * 
 * @author Qingchun Team
 */
@Service
public class LoanProductService {

    @Autowired
    private LoanProductMapper loanProductMapper;

    /**
     * 获取贷款产品列表（用户端，只返回上架产品）
     */
    public List<LoanProduct> listLoanProducts(String productType, Integer page, Integer size) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 6 : size;
        QueryWrapper<LoanProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "ACTIVE"); // ACTIVE表示上架
        if (productType != null && !productType.trim().isEmpty()) {
            wrapper.eq("category", productType.trim());
        }
        wrapper.orderByAsc("id");
        int offset = (currentPage - 1) * pageSize;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        return loanProductMapper.selectList(wrapper);
    }

    /**
     * 获取所有贷款产品（管理端，包含所有状态）
     */
    public List<LoanProduct> getAllProducts() {
        QueryWrapper<LoanProduct> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        return loanProductMapper.selectList(wrapper);
    }
    
    /**
     * 获取所有贷款产品（分页）
     */
    public com.qingchun.travelloan.vo.PageResult<LoanProduct> getAllProducts(Integer page, Integer size) {
        QueryWrapper<LoanProduct> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        
        // 获取总数
        long total = loanProductMapper.selectCount(wrapper);
        
        // 分页处理
        int currentPage = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;
        int offset = (currentPage - 1) * pageSize;
        wrapper.last("LIMIT " + offset + "," + pageSize);
        
        List<LoanProduct> records = loanProductMapper.selectList(wrapper);
        
        return com.qingchun.travelloan.vo.PageResult.of(records, total, currentPage, pageSize);
    }
    
    /**
     * 获取所有产品ID列表（用于快速切换）
     */
    public List<Long> getAllProductIds() {
        QueryWrapper<LoanProduct> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.orderByDesc("created_at");
        List<LoanProduct> products = loanProductMapper.selectList(wrapper);
        return products.stream()
                .map(LoanProduct::getId)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 根据ID获取产品
     */
    public LoanProduct getProductById(Long id) {
        LoanProduct product = loanProductMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        return product;
    }

    /**
     * 创建产品
     */
    public LoanProduct createProduct(LoanProduct product) {
        // 验证必填字段
        validateProduct(product);
        
        // 检查产品编码是否已存在
        QueryWrapper<LoanProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("product_code", product.getProductCode());
        LoanProduct existing = loanProductMapper.selectOne(wrapper);
        if (existing != null) {
            throw new BusinessException("产品编码已存在");
        }

        // status字段在数据库中是TINYINT，但实体类使用String，MyBatis会自动转换
        // 为了兼容，我们使用字符串，MyBatis会根据类型转换
        // 如果数据库是TINYINT，需要设置为"1"或"0"
        // 如果数据库是VARCHAR，使用"ACTIVE"或"INACTIVE"
        // 这里假设数据库是VARCHAR类型，如果实际是TINYINT，需要调整
        product.setStatus("ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        loanProductMapper.insert(product);
        return product;
    }

    /**
     * 更新产品
     */
    public LoanProduct updateProduct(Long id, LoanProduct product) {
        LoanProduct existing = getProductById(id);
        
        // 验证必填字段
        validateProduct(product);
        
        // 如果修改了产品编码，检查是否与其他产品冲突
        if (!existing.getProductCode().equals(product.getProductCode())) {
            QueryWrapper<LoanProduct> wrapper = new QueryWrapper<>();
            wrapper.eq("product_code", product.getProductCode());
            wrapper.ne("id", id);
            LoanProduct conflict = loanProductMapper.selectOne(wrapper);
            if (conflict != null) {
                throw new BusinessException("产品编码已被其他产品使用");
            }
        }

        product.setId(id);
        product.setUpdatedAt(LocalDateTime.now());
        loanProductMapper.updateById(product);
        return product;
    }

    /**
     * 上架产品
     */
    public void activateProduct(Long id) {
        LoanProduct product = getProductById(id);
        // 如果数据库status是TINYINT，使用"1"，如果是VARCHAR，使用"ACTIVE"
        // 根据实际数据库schema调整
        product.setStatus("ACTIVE");
        product.setUpdatedAt(LocalDateTime.now());
        loanProductMapper.updateById(product);
    }

    /**
     * 下架产品
     */
    public void deactivateProduct(Long id) {
        LoanProduct product = getProductById(id);
        // 如果数据库status是TINYINT，使用"0"，如果是VARCHAR，使用"INACTIVE"
        product.setStatus("INACTIVE");
        product.setUpdatedAt(LocalDateTime.now());
        loanProductMapper.updateById(product);
    }

    /**
     * 删除产品
     */
    public void deleteProduct(Long id) {
        LoanProduct product = getProductById(id);
        loanProductMapper.deleteById(id);
    }

    /**
     * 验证产品必填字段
     */
    private void validateProduct(LoanProduct product) {
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new BusinessException("产品名称不能为空");
        }
        if (product.getProductCode() == null || product.getProductCode().trim().isEmpty()) {
            throw new BusinessException("产品编码不能为空");
        }
        if (product.getProductType() == null || product.getProductType().trim().isEmpty()) {
            throw new BusinessException("产品类型不能为空");
        }
        if (product.getMinAmount() == null || product.getMinAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("最低额度必须大于0");
        }
        if (product.getMaxAmount() == null || product.getMaxAmount().compareTo(product.getMinAmount()) < 0) {
            throw new BusinessException("最高额度不能小于最低额度");
        }
        if (product.getInterestRate() == null || product.getInterestRate().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessException("年化利率不能为负数");
        }
        if (product.getMinTerm() == null || product.getMinTerm() <= 0) {
            throw new BusinessException("最短期限必须大于0");
        }
        if (product.getMaxTerm() == null || product.getMaxTerm() < product.getMinTerm()) {
            throw new BusinessException("最长期限不能小于最短期限");
        }
        if (product.getInstitutionName() == null || product.getInstitutionName().trim().isEmpty()) {
            throw new BusinessException("持牌机构名称不能为空");
        }
    }
}
