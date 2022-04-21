package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper for user operations
 */
@Mapper
public interface UserMapper {

  int insertUser(@Param("user") User user);

  User selectByOpenId(@Param("openId") String openId);
}
