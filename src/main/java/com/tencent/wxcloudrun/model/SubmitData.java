package com.tencent.wxcloudrun.model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.Date;

/**
 * Attendance submit data entity
 *
 * @author dev-qiuyu
 * @date 2022/4/11
 */
@Data
public class SubmitData {

    /**
     * primary key
     */
    @Expose(serialize = false)
    private Long id;

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
     * 需要补考勤的日期
     */
    @Expose(serialize = true)
    private String targetDate;

    /**
     * 类型。1 - 迟到；2 - 早退
     */
    @Expose(serialize = true)
    private Integer type;

    /**
     * data id. 32 bytes UUID
     */
    @Expose(serialize = true)
    private String dataId;

    /**
     * flag. 0 - normal, 1 - deleted
     */
    @Expose(serialize = true)
    private Integer flag;

    /**
     * submit time
     */
    @Expose(serialize = true)
    private Date submitTime;

}
