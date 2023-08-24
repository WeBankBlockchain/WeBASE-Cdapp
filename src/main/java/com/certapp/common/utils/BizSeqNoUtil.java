package com.certapp.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class BizSeqNoUtil {

    /**
     * get bizSeqNo
     */
    public static String getBizSeqNo() {
        String bizSeqNo = UUID.randomUUID().toString().replace("-", "");
        log.info("gen new bizSeq:{}", bizSeqNo);
        return bizSeqNo;
    }

}
