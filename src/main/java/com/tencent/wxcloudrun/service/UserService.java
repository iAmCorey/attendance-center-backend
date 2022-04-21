package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.SubmitData;
import com.tencent.wxcloudrun.model.User;

import java.util.List;

/**
 * User Service
 *
 * @author dev-qiuyu
 * @date 2022/4
 */
public interface UserService {

    /**
     * 插入数据
     *
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * select user by openId
     * @param openId
     * @return
     */
    User selectByOpenId(String openId);

}
