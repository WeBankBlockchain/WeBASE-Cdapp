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
package com.certapp.common.entity;

import com.certapp.common.code.RetCode;
import lombok.Data;

@Data
public class BaseResponse {

    protected int code;
    protected String msg;
    protected String bizSeqNo;
    protected Object result;

    public BaseResponse() {}

    public BaseResponse(RetCode retcode) {
        this.code = retcode.getCode();
        this.msg = retcode.getMsg();
    }

    public BaseResponse(RetCode retcode, String bizSeqNo) {
        this.code = retcode.getCode();
        this.msg = retcode.getMsg();
        this.bizSeqNo = bizSeqNo;
    }

    public BaseResponse(RetCode retcode, Object result) {
        this.code = retcode.getCode();
        this.msg = retcode.getMsg();
        this.result = result;
    }

    public void setRetCode(RetCode retCode) {
        this.code = retCode.getCode();
        this.msg = retCode.getMsg();
    }

    public void setRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public boolean failed() {
        return code != 0;
    }
}
