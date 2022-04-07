package com.tencent.wxcloudrun.controller;

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
  final Logger logger;

  public AttendanceController(@Autowired CounterService counterService) {
    this.counterService = counterService;
    this.logger = LoggerFactory.getLogger(AttendanceController.class);
  }


  /**
   * submit attendance
   * @param req request body
   * @return API response json
   */
  @PostMapping(value = "/attendanceService/submit")
  ApiResponse submitAttendance(@RequestBody String req) {
    logger.info("Receive submit attendance request: {}", req);



    return ApiResponse.ok(req);
  }
  
}