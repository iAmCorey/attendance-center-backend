package com.tencent.wxcloudrun.controller;

import com.google.gson.*;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.SubmitData;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.CounterService;
import com.tencent.wxcloudrun.service.SubmitDataService;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.util.HttpMsgUtils;
import com.tencent.wxcloudrun.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

/**
 * Controller for attendance miniprogram
 *
 * @author dev-qiuyu
 * @date 2022/4
 */
@RestController
public class AttendanceController {

    final SubmitDataService submitDataService;
    final UserService userService;
    final Logger log;

    // 迟到
    private static final int TYPE_LATE = 1;
    // 早退
    private static final int TYPE_EARLY = 2;

    // user & data flag - normal
    private static final int FLAG_NORMAL = 0;
    // user & data flag - deleted
    private static final int FLAG_DELETED = 1;

    // app info
    /**
     * app id
     */
    private static final String APP_ID = "wx3c598f6a14c115c0";

    /**
     * app secret
     */
    private static final String APP_SECRET = "467be81882cb8db18093a33794e9d886";

    /**
     * code2session request url
     */
    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    public AttendanceController(@Autowired SubmitDataService submitDataService, @Autowired UserService userService) {
        this.submitDataService = submitDataService;
        this.userService = userService;
        this.log = LoggerFactory.getLogger(AttendanceController.class);
    }


    /**
     * submit attendance
     *
     * @param req request body
     * @return API response json
     */
    @PostMapping(value = "/attendanceService/submit")
    ApiResponse submitAttendance(@RequestBody String req) {
        log.info("Receive submit attendance request: {}", req);

        Gson gson = new GsonBuilder()
                // 禁止unicode转义
                .disableHtmlEscaping()
                .create();

        try {
            JsonObject reqJs = gson.fromJson(req, JsonObject.class);

            // 姓名和员工编号
            String name = reqJs.get("name").getAsString();
            String id = reqJs.get("id").getAsString();
            log.info("姓名 & 员工编号: {}, {}", name, id);

            List<SubmitData> submitDataList = new ArrayList<>();

            // 迟到日期
            JsonElement lateDateJs = reqJs.get("lateDateList");
            if (lateDateJs != null) {
                JsonArray lateDateList = lateDateJs.getAsJsonArray();
                if (lateDateList.size() > 0) {
                    log.info("迟到日期: " + lateDateList);
                }

                for (JsonElement lateDate : lateDateList) {
                    SubmitData submitData = new SubmitData();

                    submitData.setEmployeeName(name);
                    submitData.setEmployeeId(id);

                    submitData.setType(TYPE_LATE);
                    submitData.setTargetDate(lateDate.getAsString());

                    // yyyy-MM-dd
                    Integer year = Integer.parseInt(lateDate.getAsString().substring(0, 4));
                    Integer month = Integer.parseInt(lateDate.getAsString().substring(5, 7));
                    Integer day = Integer.parseInt(lateDate.getAsString().substring(8, 10));
                    submitData.setTargetYear(year);
                    submitData.setTargetMonth(month);
                    submitData.setTargetDay(day);

                    submitData.setDataId(RandomUtils.getUUID());
                    submitData.setFlag(FLAG_NORMAL);
                    submitData.setSubmitTime(new Date());

                    submitDataList.add(submitData);
                }
            }


            // 早退日期
            JsonElement earlyDateJs = reqJs.get("earlyDateList");
            if (earlyDateJs != null) {
                JsonArray earlyDateList = earlyDateJs.getAsJsonArray();
                if (earlyDateList.size() > 0) {
                    log.info("早退日期: ", earlyDateList);
                }

                for (JsonElement earlyDate : earlyDateList) {
                    SubmitData submitData = new SubmitData();

                    submitData.setEmployeeName(name);
                    submitData.setEmployeeId(id);

                    submitData.setType(TYPE_EARLY);
                    submitData.setTargetDate(earlyDate.getAsString());

                    // yyyy-MM-dd
                    Integer year = Integer.parseInt(earlyDate.getAsString().substring(0, 4));
                    Integer month = Integer.parseInt(earlyDate.getAsString().substring(5, 7));
                    Integer day = Integer.parseInt(earlyDate.getAsString().substring(8, 10));
                    submitData.setTargetYear(year);
                    submitData.setTargetMonth(month);
                    submitData.setTargetDay(day);

                    submitData.setDataId(RandomUtils.getUUID());
                    submitData.setFlag(FLAG_NORMAL);
                    submitData.setSubmitTime(new Date());

                    submitDataList.add(submitData);
                }
            }

            // 插入数据库
            log.info("提交补考勤数量: {}", submitDataList.size());

            int insertRes = submitDataService.insertBatchData(submitDataList);

            log.info("插入数据库结果: {}", insertRes);

            if (insertRes == submitDataList.size()) {
                return ApiResponse.ok("提交成功");
            } else {
                return ApiResponse.error("提交失败。插入结果：" + insertRes);
            }

        } catch (JsonSyntaxException jsonSyntaxException) {
            log.warn(jsonSyntaxException.getMessage());
            return ApiResponse.error("JSON解析失败 / JSON parse fail");
        }

    }

    /**
     * delete attendance record
     *
     * @param req request body
     * @return API response json
     */
    @PostMapping(value = "/attendanceService/deleteRecord")
    ApiResponse deleteAttendance(@RequestBody String req) {
        log.info("Receive delete attendance request: {}", req);

        Gson gson = new GsonBuilder()
                // 禁止unicode转义
                .disableHtmlEscaping()
                .create();

        try {
            JsonObject reqJs = gson.fromJson(req, JsonObject.class);

            // name, id, type, date
            String name = reqJs.get("name").getAsString();
            String id = reqJs.get("id").getAsString();
            String type = reqJs.get("type").getAsString();
            String date=  reqJs.get("date").getAsString();

            log.info("删除记录：姓名 & 员工编号 & 类型 & 日期: {}, {}, {}, {}", name, id, type, date);

            int deleteRes = submitDataService.deleteRecord(name, id, type, date);
            log.info("Delete result: {}", deleteRes);

            if (deleteRes > 0) {
                return ApiResponse.ok("删除成功");
            } else {
                return ApiResponse.error("删除失败");
            }


        } catch (JsonSyntaxException jsonSyntaxException) {
            log.warn(jsonSyntaxException.getMessage());
            return ApiResponse.error("JSON解析失败 / JSON parse fail");
        }

    }

    /**
     * get openid and return records
     *
     * @param code request code
     * @return API response json
     */
    @PostMapping(value = "/attendanceService/getRecords")
    ApiResponse getRecords(@RequestBody String code) {
        log.info("Receive query attendance records request: {}", code);

        Gson gson = new GsonBuilder()
                // 禁止unicode转义
                .disableHtmlEscaping()
                .create();

        try {
            if (null == code || code.isEmpty()) {
                log.warn("code is null or empty");
                return ApiResponse.error("获取失败");
            }

            String url = CODE2SESSION_URL + "?appid=" + APP_ID + "&secret=" + APP_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
            String charSet = StandardCharsets.UTF_8.name();
            String response = HttpMsgUtils.httpGet(url, charSet);

            log.info("code2session response: {}", response);

            JsonObject resJs = gson.fromJson(response, JsonObject.class);

            if (resJs.get("openid") == null) {
                log.info("获取openid失败");
                return ApiResponse.error("获取失败");
            }

            String openId = resJs.get("openid").getAsString();
            log.info("openid: {}", openId);

            User user = userService.selectByOpenId(openId);

            if (null == user) {
                log.info("no user found with this openId");
                return ApiResponse.notfound("用户不存在");

            }

            List<SubmitData> submitDataList = submitDataService.selectDateByNameAndId(user.getEmployeeName(), user.getEmployeeId());
            log.info("已提交的记录: {}", submitDataList);

            return ApiResponse.ok(submitDataList);

        } catch (JsonSyntaxException jsonSyntaxException) {
            log.warn(jsonSyntaxException.getMessage());
            return ApiResponse.error("JSON解析失败 / JSON parse fail");
        }

    }


    /**
     * bind a new user
     *
     * @param req request body
     * @return API response json
     */
    @PostMapping(value = "/attendanceService/bindUser")
    ApiResponse bindUser(@RequestBody String req) {
        log.info("Receive query attendance records request: {}", req);

        Gson gson = new GsonBuilder()
                // 禁止unicode转义
                .disableHtmlEscaping()
                .create();

        try {
            JsonObject reqJs = gson.fromJson(req, JsonObject.class);

            // 姓名和员工编号
            String name = reqJs.get("name").getAsString();
            String id = reqJs.get("id").getAsString();

            // code
            String code = reqJs.get("code").getAsString();

            log.info("姓名 & 员工编号: {}, {}", name, id);

            String url = CODE2SESSION_URL + "?appid=" + APP_ID + "&secret=" + APP_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
            String charSet = StandardCharsets.UTF_8.name();
            String response = HttpMsgUtils.httpGet(url, charSet);

            log.info("code2session response: {}", response);

            JsonObject resJs = gson.fromJson(response, JsonObject.class);

            if (resJs.get("openid") == null) {
                log.info("获取openid失败");
                return ApiResponse.error("获取失败");
            }

            String openId = resJs.get("openid").getAsString();
            log.info("openId: {}", openId);

            User user = new User();
            user.setUserId(RandomUtils.getUUID());
            user.setEmployeeName(name);
            user.setEmployeeId(id);
            user.setOpenId(openId);
            user.setFlag(FLAG_NORMAL);

            int insertRes = userService.insertUser(user);
            log.info("insert user result: {}", insertRes);

            if (insertRes == 1) {
                return ApiResponse.ok("绑定成功");
            } else {
                return ApiResponse.error("绑定失败");
            }

        } catch (JsonSyntaxException jsonSyntaxException) {
            log.warn(jsonSyntaxException.getMessage());
            return ApiResponse.error("JSON解析失败 / JSON parse fail");
        } catch (Exception e) {
            log.warn(e.getMessage());
            return ApiResponse.error("您已绑定过");
        }

    }

}