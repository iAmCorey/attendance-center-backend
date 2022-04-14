package com.tencent.wxcloudrun.dto;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.google.gson.JsonObject;
import com.tencent.wxcloudrun.model.ExcelData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Excel data listener
 *
 * @author dev-qiuyu
 * @date 2022/4/14
 */
@Slf4j
public class ExcelDataListener implements ReadListener<ExcelData> {

    /**
     * 每隔这么多条存储数据库
     */
    private static final int BATCH_COUNT = 5;



    /**
     * invoked when read a row
     * @param excelData
     * @param analysisContext
     */
    @Override
    public void invoke(ExcelData excelData, AnalysisContext analysisContext) {
        log.info("Read a data: " + excelData.toString());
    }

    /**
     * invoked when all data read
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("Finish reading data");
    }

}
