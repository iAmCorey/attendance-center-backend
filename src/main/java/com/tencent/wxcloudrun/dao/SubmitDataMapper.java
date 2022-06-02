package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SubmitData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubmitDataMapper {

  int insertData(@Param("data")SubmitData submitData);

  int insertBatchData(@Param("dataList") List<SubmitData> submitDataList);

  List<SubmitData> selectDistinctDateByNameAndIdAndFlag(@Param("employeeName") String employeeName, @Param("employeeId") String employeeId, @Param("targetMonths") List<Integer> targetMonths, @Param("flags") List<Integer> flags);

  int deleteRecords(@Param("employeeName") String name, @Param("employeeId") String id, @Param("type") int type, @Param("date") String date);
}
