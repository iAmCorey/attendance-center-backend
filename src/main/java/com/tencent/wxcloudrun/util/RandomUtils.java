package com.tencent.wxcloudrun.util;

import java.util.UUID;

/**
 * Generate random number
 * @author dev-qiuyu
 * @date 2021/09
 */
public class RandomUtils {
    private RandomUtils() {
        throw new Error("工具类不允许实例化");
    }

    /**
     * generate a 32byte uuid number
     * @return 32byte uuid number
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
