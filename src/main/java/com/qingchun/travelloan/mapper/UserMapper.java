package com.qingchun.travelloan.mapper;

import com.qingchun.travelloan.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author Qingchun Team
 */
@Mapper
public interface UserMapper {

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 根据角色查询用户列表
     */
    List<User> selectByRole(@Param("role") String role);

    /**
     * 统计用户数量
     */
    int count();
}
