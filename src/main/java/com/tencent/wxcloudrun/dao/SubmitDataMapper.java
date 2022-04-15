package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SubmitData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubmitDataMapper {

  int insertData(@Param("data")SubmitData submitData);

  int insertBatchData(@Param("dataList") List<SubmitData> submitDataList);

  List<SubmitData> selectDistinctDateByNameAndId(@Param("employeeName") String employeeName, @Param("employeeId") String employeeId);
}
