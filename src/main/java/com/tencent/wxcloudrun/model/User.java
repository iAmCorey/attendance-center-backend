package com.tencent.wxcloudrun.model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.Date;

/**
 * User
 *
 * @author dev-qiuyu
 * @date 2022/4/20
 */
@Data
public class User {

    /**
     * primary key
     */
    @Expose(serialize = false)
    private Long id;

    /**
     * user id
     */
    @Expose(serialize = true)
    private String userId;

    /**
     * employee name
     */
    @Expose(serialize = true)
    private String employeeName;

    /**
     * 8 bytes long employeeId
     */
    @Expose(serialize = true)
    private String employeeId;

    /**
     * open id
     */
    @Expose(serialize = true)
    private String openId;

    /**
     * flag. 0 - normal, 1 - deleted
     */
    @Expose(serialize = true)
    private Integer flag;

    /**
     * extend information
     */
    @Expose(serialize = true)
    private String extendInfo;

    /**
     * create time
     */
    @Expose(serialize = false)
    private Date createTime;

}
