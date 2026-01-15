package com.qingchun.travelloan.dto;

import com.qingchun.travelloan.entity.Guarantor;
import com.qingchun.travelloan.entity.LoanApplication;
import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.entity.UserIdentity;
import com.qingchun.travelloan.entity.UserLocation;
import lombok.Data;

import java.util.List;

@Data
public class ProfileDTO {
    private User user;
    private UserIdentity identity;
    private Guarantor guarantor;
    private List<LoanApplication> loans;
    private UserLocation location;
    private Boolean identityVerified;
    private Boolean guarantorCompleted;
}
