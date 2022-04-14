package com.tencent.wxcloudrun;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.tencent.wxcloudrun.dto.ExcelDataListener;
import com.tencent.wxcloudrun.model.ExcelData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * Test for Excel operations.
 *
 * @author dev-qiuyu
 * @date 2022/4/14
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ExcelTest {

    @Test
    void test() {
        System.out.println("Hello World!");
    }

    @Test
    void readExcelTest() {
        String fileName = "/Users/corey/Desktop/test.xlsx";
        EasyExcel.read(fileName, ExcelData.class, new ReadListener<ExcelData>() {

            @Override
            public void invoke(ExcelData excelData, AnalysisContext analysisContext) {
                log.info("excelData: {}", excelData);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                log.info("finish");
            }
        }).sheet().doRead();


//        EasyExcel.read(fileName, ExcelData.class, new ExcelDataListener()).sheet().doRead();
    }
}
