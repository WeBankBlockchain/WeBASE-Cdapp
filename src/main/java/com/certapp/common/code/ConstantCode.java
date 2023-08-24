/**
 * Copyright 2014-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.certapp.common.code;

import java.util.Arrays;
import java.util.List;

/**
 * AAAA-B-CCC. <br/>
 * A:system number. <br/>
 * B:error level <br/>
 * 0:business exception <br/>
 * 1:system exception <br/>
 * 2:param exception <br/>
 * C:error code <br/>
 */
public class ConstantCode {

    /* return success */
    public static final RetCode SUCCESS = RetCode.mark(0, "success");

    /* system exception */
    public static final RetCode SYSTEM_EXCEPTION = RetCode.mark(10001000, "system exception");
    public static final RetCode DATABASE_EXCEPTION = RetCode.mark(10001001, "database exception");
    public static final RetCode SYSTEM_BUSY = RetCode.mark(10001002, "system is busy. please try again later");
    public static final RetCode REMOTE_SERVER_EXCEPTION = RetCode.mark(10001003, "remote server exception");
    
    /* param exception */
    public static final RetCode PARAM_EXCEPTION = RetCode.mark(10002000, "param exception");
    public static final String PARAMETER_TOO_LONG = "{\"code\":10002001,\"msg\":\"parameter is too long\"}";
    public static final String USERID_IS_EMPTY = "{\"code\":10002002,\"msg\":\"userId can not be empty\"}";

    /* business exception */
    public static final RetCode SEND_TO_CHAIN_RETURN_RECEIPT_NULL = RetCode.mark(10003001, "send to chain and get null receipt");
    public static final RetCode SEND_TO_CHAIN_RETURN_RECEIPT_ERROR = RetCode.mark(10003002, "send to chain and get error receipt");
    public static final RetCode CREATE_QRCODE_ERROR = RetCode.mark(10003003, "create qrCode error");
    public static final RetCode CREATE_CERT_ERROR = RetCode.mark(10003004, "create cert error");
    public static final RetCode FILE_NOT_EXISTS = RetCode.mark(10003005, "file not exists");
    public static final RetCode INVALID_CERTSTATE = RetCode.mark(10003006, "invalid certState");
    public static final RetCode UPDATE_PIC_PREVIEW_ERROR = RetCode.mark(10003007, "update pic preview error");
    public static final RetCode CREATE_CERTPIC_ERROR = RetCode.mark(10003008, "create cert pic error");
    public static final RetCode DELETE_CERT_ERROR = RetCode.mark(10003009, "delete cert error");
    public static final RetCode FILE_UPLOAD_ERROR = RetCode.mark(10003010, "file upload error");
    public static final RetCode READ_EXCEL_ERROR = RetCode.mark(10003011, "read excel error");
    public static final RetCode UPDATE_CERT_STATE_ERROR = RetCode.mark(10003012, "update cert state error, cert none cochain");
    public static final RetCode CERT_COCHAIN_FAIL = RetCode.mark(10003013, "cert cochain fail");
    public static final RetCode CERT_NONE_EXIST = RetCode.mark(10003014, "cert none exist");
    public static final RetCode CERT_DELETE_ERROR = RetCode.mark(10003015, "conchain or publish can not delete");
    public static final RetCode INVALID_CHECK_CODE = RetCode.mark(10003016, "invalid checkCode");
    public static final RetCode CERT_NOT_PUBLISH = RetCode.mark(10003017, "cert not publish");
    public static final RetCode CERT_NAME_THE_SAME = RetCode.mark(10003018, "cert name has bean in use");

    public static final RetCode PASSWORD_ERROR = RetCode.mark(209255, "username or password error");
    public static final RetCode NEW_PWD_EQUALS_OLD = RetCode.mark(209360, "the new password cannot be same as old");

    public final static List<RetCode> CODE_ALARM_LIST = Arrays.asList(SYSTEM_EXCEPTION, DATABASE_EXCEPTION,
            REMOTE_SERVER_EXCEPTION, SEND_TO_CHAIN_RETURN_RECEIPT_NULL, SEND_TO_CHAIN_RETURN_RECEIPT_ERROR
            );

    public static boolean isAlarmCode(int code) {
        long count = CODE_ALARM_LIST.stream().filter(c -> c.getCode() == code).count();
        return count > 0;
    }

}
