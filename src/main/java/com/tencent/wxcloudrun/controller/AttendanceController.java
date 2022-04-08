package com.tencent.wxcloudrun.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controller for attendance miniprogram
 * @author dev-qiuyu
 * @date 2022/4
 */
@RestController
public class AttendanceController {

  final CounterService counterService;
  final Logger log;

  public AttendanceController(@Autowired CounterService counterService) {
    this.counterService = counterService;
    this.log = LoggerFactory.getLogger(AttendanceController.class);
  }


  /**
   * submit attendance
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

      // employee name
      String name = reqJs.get("name").getAsString();
      // employee id
      String id = reqJs.get("id").getAsString();
      // 迟到日期
      String lateDate = reqJs.get("lateDateList").getAsString();
      // 早退日期
      String earlyDate = reqJs.get("earlyDateList").getAsString();




      return ApiResponse.ok(req);

    } catch (JsonSyntaxException jsonSyntaxException) {
      log.warn(jsonSyntaxException.getMessage());
      return ApiResponse.error("JSON解析失败 / JSON parse fail");
    }

  }
  
}