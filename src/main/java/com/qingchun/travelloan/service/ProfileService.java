package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingchun.travelloan.dto.ProfileDTO;
import com.qingchun.travelloan.entity.Guarantor;
import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.entity.UserIdentity;
import com.qingchun.travelloan.mapper.GuarantorMapper;
import com.qingchun.travelloan.mapper.LoanApplicationMapper;
import com.qingchun.travelloan.mapper.UserIdentityMapper;
import com.qingchun.travelloan.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserIdentityMapper userIdentityMapper;
    @Autowired
    private GuarantorMapper guarantorMapper;
    @Autowired
    private LoanApplicationMapper loanApplicationMapper;

    public ProfileDTO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        UserIdentity identity = userIdentityMapper.selectOne(new QueryWrapper<UserIdentity>().eq("user_id", userId));
        Guarantor guarantor = guarantorMapper.selectOne(new QueryWrapper<Guarantor>().eq("user_id", userId));
        List<LoanApplication> loans = loanApplicationMapper.selectList(
                new QueryWrapper<LoanApplication>().eq("user_id", userId).orderByDesc("apply_time"));

        ProfileDTO dto = new ProfileDTO();
        dto.setUser(user);
        dto.setIdentity(identity);
        dto.setGuarantor(guarantor);
        dto.setLoans(loans);
        dto.setIdentityVerified(identity != null && "VERIFIED".equalsIgnoreCase(identity.getVerificationStatus()));
        dto.setGuarantorCompleted(guarantor != null
                && guarantor.getName() != null
                && guarantor.getIdCard() != null
                && Boolean.TRUE.equals(guarantor.getAgreementSigned()));
        return dto;
    }
}
