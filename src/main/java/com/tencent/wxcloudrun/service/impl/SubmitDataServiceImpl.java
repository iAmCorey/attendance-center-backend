package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SubmitDataMapper;
import com.tencent.wxcloudrun.model.SubmitData;
import com.tencent.wxcloudrun.service.SubmitDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for SubmitData.
 *
 * @author dev-qiuyu
 * @date 2022/4
 */
@Service
public class SubmitDataServiceImpl implements SubmitDataService {

  @Autowired
  private SubmitDataMapper submitDataMapper;

  @Override
  @Transactional
  public int insertData(SubmitData data) {
    return submitDataMapper.insertData(data);
  }

  @Override
  @Transactional
  public int insertBatchData(List<SubmitData> dataList) {
    return submitDataMapper.insertBatchData(dataList);
  }
}
