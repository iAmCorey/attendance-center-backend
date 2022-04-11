package com.tencent.wxcloudrun.model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Attendance record entity
 *
 * @author dev-qiuyu
 * @date 2022/4/11
 */
@Data
public class Record {

    /**
     * primary key
     */
    @Expose(serialize = false)
    private Long id;

    /**
     * record id. 32 bytes UUID
     */
    @Expose(serialize = true)
    private String recordId;

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
     * 迟到日期
     */
    @Expose(serialize = true)
    private List<Date> lateDate;

    /**
     * 早退日期
     */
    @Expose(serialize = true)
    private List<Date> earlyDate;

}
