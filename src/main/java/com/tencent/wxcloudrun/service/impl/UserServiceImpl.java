package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SubmitDataMapper;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.model.SubmitData;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.SubmitDataService;
import com.tencent.wxcloudrun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for User.
 *
 * @author dev-qiuyu
 * @date 2022/4
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public User selectByOpenId(String openId) {
        return userMapper.selectByOpenId(openId);
    }
}
