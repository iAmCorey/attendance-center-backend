package com.tencent.wxcloudrun;

import com.tencent.wxcloudrun.service.SubmitDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test for submitData operations.
 *
 * @author dev-qiuyu
 * @date 2022/4/14
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SubmitDataTest {

    @Autowired
    private SubmitDataService submitDataService;

    @Test
    void test() {
        System.out.println("Hello World!");
    }

    @Test
    void distinctSelectTest() {
        String name = "叶雨田";
        String id = "26626906";

        System.out.println(submitDataService.selectDateByNameAndId(name, id));

    }
}
