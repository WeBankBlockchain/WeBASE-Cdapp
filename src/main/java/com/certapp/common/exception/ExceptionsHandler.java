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

import com.certapp.common.code.ConstantCode;
import com.certapp.common.code.RetCode;
import com.certapp.common.entity.BaseResponse;
import com.certapp.common.utils.JacksonUtils;
import com.certapp.common.utils.LogUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

/**
 * catch an handler exception.
 */
@ControllerAdvice
@Log4j2
public class ExceptionsHandler {

    /**
     * catch：BaseException.
     */
    @ResponseBody
    @ExceptionHandler(value = BaseException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse myExceptionHandler(BaseException baseException) {
        RetCode retCode = Optional.ofNullable(baseException).map(BaseException::getRetCode)
                .orElse(ConstantCode.SYSTEM_EXCEPTION);
        BaseResponse baseResponse = new BaseResponse(retCode, baseException.getBizSeqNo());
        log.error("OnError:business exception bizSeqNo:{} return:{}", baseException.getBizSeqNo(),
                JacksonUtils.toJSONString(baseResponse));
        LogUtils.getMonitorLogger().error("OnError:business exception return:{}",
                JacksonUtils.toJSONString(baseResponse));
        return baseResponse;
    }

    /**
     * catch：RuntimeException.
     */
    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public BaseResponse exceptionHandler(RuntimeException exc) {
        // default system exception
        RetCode retCode = new RetCode(ConstantCode.SYSTEM_EXCEPTION.getCode(), exc.getMessage());
        BaseResponse bre = new BaseResponse(retCode);
        log.error("OnError:system RuntimeException return:{}", JacksonUtils.toJSONString(bre));
        LogUtils.getMonitorLogger().warn("OnError:RuntimeException return:{}",
                JacksonUtils.toJSONString(bre));
        return bre;
    }
}
