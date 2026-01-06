package com.qingchun.travelloan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingchun.travelloan.entity.UserCertificate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户证书Mapper接口
 *
 * @author Qingchun Team
 */
@Mapper
public interface UserCertificateMapper extends BaseMapper<UserCertificate> {
    
    /**
     * 根据证书类型和来源ID查询证书
     */
    UserCertificate selectByTypeAndSourceId(
            @Param("certificateType") String certificateType,
            @Param("sourceId") Long sourceId
    );
}

