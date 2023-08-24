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
package com.certapp.common.controller;


import com.certapp.common.code.ConstantCode;
import com.certapp.common.code.RetCode;
import com.certapp.common.exception.BaseException;
import com.certapp.common.utils.JacksonUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;


@Log4j2
public class BaseController {

    /**
     * 参数校验
     * 
     * @param bindingResult 注解校验入参的结果
     */
    protected void checkBindResult(BindingResult bindingResult) {
        // 没有错误信息
        if (!bindingResult.hasErrors()) {
            return;
        }
        // 获取参数校验的错误信息
        String errorMsg = getParamValidFaildMessage(bindingResult);
        if (StringUtils.isBlank(errorMsg)) {
            log.warn("onWarning:param exception. errorMsg is empty");
            throw new BaseException(ConstantCode.PARAM_EXCEPTION);
        }

        RetCode retCode = null;
        try {
            retCode = JacksonUtils.toJavaObject(errorMsg, RetCode.class);
        } catch (Exception ex) {
            log.warn("onWarning:retCodeJson convert error:" + ex.getMessage());
            throw new BaseException(ConstantCode.PARAM_EXCEPTION);
        }
        throw new BaseException(retCode);
    }

    /**
     * 获取参数校验的错误信息
     */
    private String getParamValidFaildMessage(BindingResult bindingResult) {
        // 获取所有的错误信息
        List<ObjectError> errorList = bindingResult.getAllErrors();
        if (errorList == null) {
            log.warn("onWarning:errorList is empty!");
            return null;
        }
        ObjectError objectError = errorList.get(0);
        if (objectError == null) {
            log.warn("onWarning:objectError is empty!");
            return null;
        }
        // 返回错误信息
        return objectError.getDefaultMessage();
    }
}
