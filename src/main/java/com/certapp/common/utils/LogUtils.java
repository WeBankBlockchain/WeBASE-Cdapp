package com.certapp.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类 todo 加入监控埋点和告警error；统一错误日志格式
 */
public class LogUtils {

    private static final Logger MONILOGGER = LoggerFactory.getLogger("appmonitor");

    public static Logger getMonitorLogger() {
        return MONILOGGER;
    }

}
