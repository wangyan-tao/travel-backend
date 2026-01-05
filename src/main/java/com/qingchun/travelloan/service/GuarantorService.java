package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.entity.Guarantor;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.GuarantorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GuarantorService {
    @Autowired
    private GuarantorMapper guarantorMapper;

    public Guarantor submit(Guarantor request, Long userId) {
        if (request == null) {
            throw new BusinessException("担保人信息不能为空");
        }
        validateRequest(request);

        Guarantor g = guarantorMapper.selectOne(new QueryWrapper<Guarantor>().eq("user_id", userId));
        if (g == null) {
            g = new Guarantor();
            g.setUserId(userId);
        }

        g.setName(request.getName());
        g.setIdCard(request.getIdCard());
        g.setRelationship(request.getRelationship());
        g.setPhone(request.getPhone());
        g.setWorkUnit(request.getWorkUnit());
        g.setIdCardFrontUrl(request.getIdCardFrontUrl());
        g.setIdCardBackUrl(request.getIdCardBackUrl());
        g.setAgreementSigned(Boolean.TRUE.equals(request.getAgreementSigned()));
        g.setAgreementSignedAt(Boolean.TRUE.equals(request.getAgreementSigned()) ? LocalDateTime.now() : null);

        if (g.getId() == null) {
            guarantorMapper.insert(g);
        } else {
            guarantorMapper.updateById(g);
        }
        return g;
    }

    public Guarantor info(Long userId) {
        return guarantorMapper.selectOne(new QueryWrapper<Guarantor>().eq("user_id", userId));
    }

    private void validateRequest(Guarantor r) {
        if (isBlank(r.getName()) || isBlank(r.getIdCard()) || isBlank(r.getRelationship()) || isBlank(r.getPhone())) {
            throw new BusinessException("请完整填写必填信息");
        }
        if (isBlank(r.getIdCardFrontUrl()) || isBlank(r.getIdCardBackUrl())) {
            throw new BusinessException("请上传身份证正反面照片");
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
