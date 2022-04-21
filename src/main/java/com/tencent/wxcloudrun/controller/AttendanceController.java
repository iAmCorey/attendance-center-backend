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

                    submitData.setDataId(RandomUtils.getUUID());
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

                    submitData.setDataId(RandomUtils.getUUID());
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

}