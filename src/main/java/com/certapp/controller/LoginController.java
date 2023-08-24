/**
 * Copyright 2014-2020  the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.certapp.controller;

import com.certapp.common.code.ConstantCode;
import com.certapp.common.code.RetCode;
import com.certapp.common.entity.BaseResponse;
import com.certapp.service.LoginService;
import com.certapp.dao.entity.TbUser;
import com.certapp.entity.NewPasswordInfo;
import com.certapp.entity.request.UserReq;
import com.certapp.common.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
@Log4j2
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 登录
     * @param req
     * @return
     */
    @PostMapping
    public BaseResponse userLogin(@RequestBody UserReq req) {
        log.debug("userLogin-->" + JsonUtils.toJSONString(req));
        TbUser result = loginService.login(req.getName(), req.getPwd());
        log.debug("userLogin result-->" + JsonUtils.toJSONString(result));
        if (result != null) {
            return new BaseResponse(ConstantCode.SUCCESS, result);
        }
        RetCode retCode = ConstantCode.PASSWORD_ERROR;
        return new BaseResponse(retCode);
    }

    /**
     * 修改密码
     * @param req
     * @return
     */
    @PatchMapping("/updatePwd")
    public BaseResponse updatePassword(@RequestBody NewPasswordInfo req) {
        log.info("start exec method [updatePassword]. id:{}", JsonUtils.objToString(req.getId()));
        int row = loginService.updatePassword(req.getId(), req.getNewPwd(), req.getOldPwd());
        log.info("success exec method [updateDeveloperPassword].");
        return new BaseResponse(ConstantCode.SUCCESS, row);
    }
}
