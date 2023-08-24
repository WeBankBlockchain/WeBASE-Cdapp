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
import com.certapp.common.entity.BaseResponse;
import com.certapp.common.utils.BizSeqNoUtil;
import com.certapp.common.utils.JsonUtils;
import com.certapp.entity.FiledInfoVo;
import com.certapp.entity.QueryPage;
import com.certapp.entity.QueryResultPage;
import com.certapp.entity.request.CertConfigReq;
import com.certapp.entity.request.QueryResultPageReq;
import com.certapp.entity.response.*;
import com.certapp.service.CertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Api(value = "证书配置", tags = "cert interface")
@Slf4j
@RestController
@RequestMapping("cert")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    /**
     * 添加/修改证书图片配置
     * @param req
     * @return
     */
    @ApiOperation(value = "添加/修改证书图片配置")
    @PostMapping("/saveCertPic")
    public BaseResponse saveCertPic(@RequestBody CertConfigReq req) {
        Instant startTime = Instant.now();
        log.info("start exec method [saveCertPic]:{}", JsonUtils.objToString(req));
        CertConfigResp certConfigResp = certificateService.saveCertPic(req);
        log.info("saveCertPic end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, certConfigResp);
    }

    /**
     * 查询证书图片配置
     * @param id
     * @return
     */
    @ApiOperation(value = "查询证书图片配置")
    @GetMapping("/queryCertPic")
    public BaseResponse queryCertPic(@RequestParam("id") Integer id) {
        Instant startTime = Instant.now();
        log.info("start exec method [queryCertPic] id:{}", id);
        CertConfigResp certConfigResp = certificateService.queryCertPic(id);
        log.info("queryCertPic end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, certConfigResp);
    }

    /**
     * 预览证书图片
     * @param id
     * @return
     */
    @ApiOperation(value = "预览证书图片")
    @GetMapping("/previewCertPic")
    public BaseResponse previewCertPic(@RequestParam("id") Integer id) {
        Instant startTime = Instant.now();
        log.info("start exec method [previewCertPic] id:{}", id);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        String req = certificateService.previewCertPic(id);
        baseResponse.setResult(req);
        log.info("previewCertPic end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * 证书列表
     * @return
     */
    @ApiOperation(value = "证书列表")
    @GetMapping("/getCertList")
    public BaseResponse getCertList() {
        Instant startTime = Instant.now();
        log.info("start exec method [getCertList] :{}");
        List<CertResp> certRespList = certificateService.getCertList();
        log.info("getCertList end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, certRespList);
    }

    /**
     * 添加/修改查询页配置
     * @param req
     * @return
     */
    @ApiOperation(value = "添加/修改查询页配置")
    @PostMapping("/saveQueryPage")
    public BaseResponse saveQueryPage(@RequestBody QueryPage req) {
        Instant startTime = Instant.now();
        log.info("start exec method [saveQueryPage]:{}", JsonUtils.objToString(req));
        QueryPage queryPage = certificateService.saveQueryPage(req);
        log.info("saveQueryPage end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, queryPage);
    }

    /**
     * 添加/修改查询结果页配置
     * @param req
     * @return
     */
    @ApiOperation(value = "添加/修改查询结果页配置")
    @PostMapping("/saveQueryResultPage")
    public BaseResponse saveQueryResultPage(@RequestBody QueryResultPageReq req) {
        Instant startTime = Instant.now();
        log.info("start exec method [saveQueryResultPage]:{}", JsonUtils.objToString(req));
        QueryResultPageResp queryResultPage = certificateService.saveQueryResultPage(req);
        log.info("saveQueryResultPage end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, queryResultPage);
    }

    /**
     * 获取查询页配置
     * @param certConfigId
     * @return
     */
    @ApiOperation(value = "获取查询页配置")
    @GetMapping("/getQueryPage")
    public BaseResponse getQueryPage(@RequestParam("certConfigId") Integer certConfigId) {
        Instant startTime = Instant.now();
        log.info("start exec method [getQueryPage] certConfigId:{}", certConfigId);
        QueryPage queryPage = certificateService.getQueryPage(certConfigId);
        log.info("getQueryPage end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, queryPage);
    }

    /**
     * 获取查询结果页配置
     * @param certConfigId
     * @return
     */
    @ApiOperation(value = "获取查询结果页配置")
    @GetMapping("/getQueryResultPage")
    public BaseResponse getQueryResultPage(@RequestParam("certConfigId") Integer certConfigId) {
        Instant startTime = Instant.now();
        log.info("start exec method [getQueryResultPage] certConfigId:{}", certConfigId);
        QueryResultPage queryResultPage = certificateService.getQueryResultPage(certConfigId);
        log.info("getQueryResultPage end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, queryResultPage);
    }



    /**
     * 导入数据（excel）,生成证书图片
     * @param certConfigId
     * @return
     * @throws Exception
     */
    @PostMapping("importData")
    public BaseResponse importData(@RequestParam("certConfigId") Integer certConfigId, @RequestParam(value = "file") MultipartFile file) {
        Instant startTime = Instant.now();
        log.info("importData start.");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        certificateService.importData(certConfigId, file);
        log.info("importData end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    /**
     * 查询证书配置字段
     * @param certConfigId
     * @return
     */
    @ApiOperation(value = "查询证书配置字段")
    @GetMapping("/queryFiledByCertId")
    public BaseResponse queryFiledByCertId(@RequestParam("certConfigId") Integer certConfigId) {
        Instant startTime = Instant.now();
        log.info("start exec method [queryFiledByCertId] certConfigId:{}", certConfigId);
        List<FiledInfoVo> filedInfoVoList = certificateService.queryFiledByCertId(certConfigId);
        log.info("queryFiledByCertId end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, filedInfoVoList);
    }

    @ApiOperation(value = "上链")
    @GetMapping("/cochain")
    public BaseResponse cochain(@RequestParam("certConfigId") Integer certConfigId) {
        String bizSeqNo = BizSeqNoUtil.getBizSeqNo();
        Instant startTime = Instant.now();
        log.info("start exec method [cochain] certConfigId:{}", certConfigId);
        certificateService.cochain(certConfigId, bizSeqNo);
        log.info("cochain end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS);
    }

    @ApiOperation(value = "删除证书配置")
    @DeleteMapping("/deleteById")
    public BaseResponse deleteById(@RequestParam("id") Integer id) {
        Instant startTime = Instant.now();
        log.info("start exec method [deleteById] id:{}", id);
        certificateService.deleteById(id);
        log.info("deleteById end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS);
    }

    /**
     * 获取导入字段数据
     * @param certConfigId
     * @return
     */
    @ApiOperation(value = "获取导入字段数据")
    @GetMapping("/getFiledInfo")
    public BaseResponse getFiledInfo(@RequestParam("certConfigId") Integer certConfigId) {
        Instant startTime = Instant.now();
        log.info("start exec method [getFiledInfo] certConfigId:{}", certConfigId);
        CertFiledInfoResp certFiledInfoResp = certificateService.getFiledInfo(certConfigId);
        log.info("getFiledInfo end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS, certFiledInfoResp);
    }

    /**
     * 下载/预览证书
     * @param id
     * @return
     */
    @ApiOperation(value = "下载/预览证书")
    @GetMapping("/downloadById")
    public BaseResponse downloadById(@RequestParam("id") Integer id) {
        Instant startTime = Instant.now();
        log.info("start exec method [downloadById] id:{}", id);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        String pic = certificateService.downloadById(id);
        baseResponse.setResult(pic);
        log.info("downloadById end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

    @ApiOperation(value = "下载所有证书")
    @GetMapping("/download")
    public BaseResponse download(@RequestParam("certConfigId") Integer certConfigId) {
        Instant startTime = Instant.now();
        log.info("start exec method [download] certConfigId:{}", certConfigId);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        List<CertPicResp> picList = certificateService.download(certConfigId);
        baseResponse.setResult(picList);
        log.info("download end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }


    @ApiOperation(value = "修改证书发布状态")
    @GetMapping("/updateCertState")
    public BaseResponse updateCertState(@RequestParam("certConfigId") Integer certConfigId, @RequestParam("state") Boolean state) {
        Instant startTime = Instant.now();
        log.info("start exec method [updateCertState] certConfigId:{}", certConfigId);
        certificateService.updateCertState(certConfigId, state);
        log.info("updateCertState end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return new BaseResponse(ConstantCode.SUCCESS);
    }

    @ApiOperation(value = "拷贝配置项")
    @GetMapping("/copeCert")
    public BaseResponse copeCert(@RequestParam("certConfigId") Integer certConfigId) {
        Instant startTime = Instant.now();
        log.info("start exec method [copeCert] certConfigId:{}", certConfigId);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        int id = certificateService.copeCert(certConfigId);
        baseResponse.setResult(id);
        log.info("copeCert end. usedTime:{}",
            Duration.between(startTime, Instant.now()).toMillis());
        return baseResponse;
    }

}
