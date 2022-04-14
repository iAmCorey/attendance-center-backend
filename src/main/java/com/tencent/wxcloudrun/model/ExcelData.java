package com.tencent.wxcloudrun.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Attendance excel data entity
 *
 * @author dev-qiuyu
 * @date 2022/4/14
 */

@Data
@EqualsAndHashCode
public class ExcelData {

    private String userId;

    private String userName;

    private String time;

    private String reason;

}
