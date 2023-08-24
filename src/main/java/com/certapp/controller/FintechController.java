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
package com.certapp.controller;

import com.certapp.common.code.ConstantCode;
import com.certapp.common.controller.BaseController;
import com.certapp.common.entity.BaseResponse;
import com.certapp.common.exception.BaseException;
import com.certapp.common.utils.*;
import com.certapp.entity.QueryResultPage;
import com.certapp.service.FintechService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Log4j2
@RestController
@RequestMapping("")
@Api(tags = {"服务接口"})
public class FintechController extends BaseController {

    private static final int PICTURE_CHECK_CODE_CHAR_NUMBER = 4;
    public final static String FILE_FORMAT = "data:image/png;base64,";

    @Autowired
    private FintechService fintechService;


    @ApiOperation(value = "模糊查询证书")
    @GetMapping("/queryFuzzyFiledInfo")
    public BaseResponse queryFuzzyFiledInfo(@RequestParam String filedInfo, @RequestParam Integer certConfigId, @RequestParam String checkCode) {
        String bizSeqNo = BizSeqNoUtil.getBizSeqNo();
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS, bizSeqNo);
        Instant startTime = Instant.now();
        log.info("{}", MonitorLog.monitorStr("start", "queryFuzzyFiledInfo", bizSeqNo, filedInfo));
        try {
            List<QueryResultPage> record = fintechService.queryFuzzyFiledInfo(filedInfo, checkCode, certConfigId, bizSeqNo);
            baseResponse.setResult(record);
        } catch (BaseException ex) {
            baseResponse.setRetCode(ex.getRetCode());
        } catch (TypeMismatchException ex) {
            baseResponse.setRetCode(ConstantCode.PARAM_EXCEPTION);
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "queryFuzzyFiledInfo", bizSeqNo, filedInfo,
                            String.valueOf(baseResponse.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(), null),
                    ex);
        } catch (Exception ex) {
            baseResponse.setRetCode(ConstantCode.SYSTEM_EXCEPTION.getCode(), ex.getMessage());
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "queryFuzzyFiledInfo", bizSeqNo, filedInfo,
                            String.valueOf(baseResponse.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(), null),
                    ex);
        }

        if (baseResponse.getCode() == ConstantCode.SUCCESS.getCode()) {
            log.info("{}",
                    MonitorLog.monitorStr("OnSuccess", "queryFuzzyFiledInfo", bizSeqNo, filedInfo,
                            String.valueOf(baseResponse.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(),
                            JacksonUtils.toJSONString(baseResponse)));
            LogUtils.getMonitorLogger()
                    .info(LogEntity.monitorStr("queryFuzzyFiledInfo", filedInfo,
                            String.valueOf(baseResponse.getCode()), baseResponse.getMsg(),
                            baseResponse.getBizSeqNo(),
                            Duration.between(startTime, Instant.now()).toMillis()));
        } else {
            log.error("{}",
                    MonitorLog.monitorStr("OnError", "queryFuzzyFiledInfo", bizSeqNo, filedInfo,
                            String.valueOf(baseResponse.getCode()),
                            Duration.between(startTime, Instant.now()).toMillis(),
                            JacksonUtils.toJSONString(baseResponse)));
            LogUtils.getMonitorLogger()
                    .error(LogEntity.monitorStr("queryFuzzyFiledInfo", filedInfo,
                            String.valueOf(baseResponse.getCode()), baseResponse.getMsg(),
                            baseResponse.getBizSeqNo(),
                            Duration.between(startTime, Instant.now()).toMillis()));
        }
        return baseResponse;
    }

    /**
     * 获取验证码
     */
    @ApiOperation(value = "获取验证码")
    @GetMapping(value = "pictureCheckCode")
    public BaseResponse getPictureCheckCode() throws Exception {
        log.info("start getPictureCheckCode");
        // random code
        String checkCode = CommonUtils.randomString(PICTURE_CHECK_CODE_CHAR_NUMBER);
        fintechService.createCode(checkCode);

        log.info("new checkCode:" + checkCode);

        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        try {
            // 得到图形验证码并返回给页面
            String base64Image = TokenImgGenerator.getBase64Image(checkCode);
            baseResponse.setResult(FILE_FORMAT + base64Image);
            log.info("end getPictureCheckCode. baseResponse:{}", JsonUtils.objToString(baseResponse));
            return baseResponse;
        } catch (Exception e) {
            log.error("fail getPictureCheckCode", e);
            throw new BaseException(ConstantCode.SYSTEM_EXCEPTION);
        }
    }

}
