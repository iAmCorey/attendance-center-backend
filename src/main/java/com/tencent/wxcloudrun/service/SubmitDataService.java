package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.SubmitData;

import java.util.List;

/**
 * SubmitData Service
 *
 * @author dev-qiuyu
 * @date 2022/4
 */
public interface SubmitDataService {

    int insertData(SubmitData data);

    int insertBatchData(List<SubmitData> dataList);

    List<SubmitData> selectDateByNameAndIdAndFlag(String name, String id, List<Integer> flags);

    int deleteRecord(String name, String id, String type, String date);

}
