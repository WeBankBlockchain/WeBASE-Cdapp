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
package com.certapp.common.exception;


import com.certapp.common.code.RetCode;

/**
 * base business exception.
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String bizSeqNo;
    private RetCode retCode;
    
    public BaseException() {}

    public BaseException(RetCode retCode) {
        super(retCode.getMsg());
        this.retCode = retCode;
    }

    public BaseException(RetCode retCode, String bizSeqNo) {
        super(retCode.getMsg());
        this.retCode = retCode;
        this.bizSeqNo = bizSeqNo;
    }
    
    public BaseException(int code, String msg, String bizSeqNo) {
        super(msg);
        this.retCode = new RetCode(code, msg);
    }

    public RetCode getRetCode() {
        return retCode;
    }

    public String getBizSeqNo() {
        return bizSeqNo;
    }
}
