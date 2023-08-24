/**
 * Copyright 2014-2022 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.certapp.service;

import com.certapp.common.code.ConstantCode;
import com.certapp.common.enums.CertState;
import com.certapp.common.exception.BaseException;
import com.certapp.common.utils.JsonUtils;
import com.certapp.dao.entity.*;
import com.certapp.dao.mapper.*;
import com.certapp.entity.FiledInfo;
import com.certapp.entity.QueryResultPage;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * FintechService.
 */
@Slf4j
@Service
public class FintechService {

    @Autowired
    private TbCertInfoMapper tbCertInfoMapper;
    @Autowired
    private TbCodeMapper tbCodeMapper;
    @Autowired
    private TbCertConfigMapper tbCertConfigMapper;
    @Autowired
    private TbQueryResultPageMapper tbQueryResultPageMapper;
    @Autowired
    private TbQueryPageMapper tbQueryPageMapper;

    public List<QueryResultPage> queryFuzzyFiledInfo(String filedInfo, String checkCode, Integer certConfigId, String bizSeqNo) {
        log.info("[DB]getByCertId. filedInfo:{}, certConfigId:{}, bizSeqNo:{}", filedInfo, certConfigId, bizSeqNo);
        TbCertConfig tbCertConfig = tbCertConfigMapper.selectByPrimaryKey(certConfigId);
        if (tbCertConfig == null) {
            return Collections.emptyList();
        }
        TbCode tbCode = tbCodeMapper.selectByCode(checkCode);
        //校验验证码
        if (tbCode == null) {
            log.error("invalid checkCode.");
            throw new BaseException(ConstantCode.INVALID_CHECK_CODE);
        }
        tbCodeMapper.deleteByPrimaryKey(tbCode.getId());
        List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectBySearchFiled(filedInfo);
        List<QueryResultPage> queryResultPageList = new ArrayList<>();
        //未发布的证书不能查询
        tbCertInfoList.stream().forEach(tbCertInfo -> {
            if (tbCertInfo.getCertState() != CertState.publish.getType()) {
                log.error("cert not publish.");
                throw new BaseException(ConstantCode.CERT_NOT_PUBLISH);
            }
            QueryResultPage queryResultPage = new QueryResultPage();
            TbQueryPage tbQueryPage = tbQueryPageMapper.selectByCertConfigId(tbCertInfo.getCertConfigId());
            TbQueryResultPage tbQueryResultPage = tbQueryResultPageMapper.selectByCertConfigId(tbCertInfo.getCertConfigId());
            if (tbQueryResultPage.getCertConfigId() == certConfigId) {
                BeanUtils.copyProperties(tbQueryResultPage, queryResultPage);
                List<FiledInfo> fields = JsonUtils.stringToObj(JsonUtils.objToString(tbQueryResultPage.getEnableFields()),
                    new TypeReference<List<FiledInfo>>() {
                    });
                String[] certInfoSplit = tbCertInfo.getFiledInfo().split(";");
                fields.stream().forEach(filed -> {
                    //显示字段赋值
                    if (filed.getFiledName().equals("certHash")) {
                        filed.setFiledDemo(tbCertInfo.getCertHash());
                    } else if (filed.getFiledName().equals("certTime")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        filed.setFiledDemo(sdf.format(tbCertInfo.getCertTime()));
                    } else if (filed.getFiledName().equals("certOrg")) {
                        filed.setFiledDemo(tbQueryPage.getCertOrg());
                    } else if (filed.getFiledName().equals("certName")) {
                        filed.setFiledDemo(tbCertConfig.getCertName());
                    } else
                        filed.setFiledDemo(certInfoSplit[Integer.parseInt(filed.getFiledSerial())]);
                });
                List<String> enableFields = new ArrayList<>();
                fields.stream().forEach(enableField -> {
                    enableFields.add(JsonUtils.objToString(enableField));
                });
                queryResultPage.setEnableFields(enableFields);
                queryResultPage.setPicPreview(tbCertInfo.getCertPic());
                queryResultPageList.add(queryResultPage);
            }
        });
        return queryResultPageList;
    }


    public void createCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.error("fail createCode. param is null");
        }
        TbCode tbCode = new TbCode();
        tbCode.setCode(code);
        tbCodeMapper.insertSelective(tbCode);
    }

}

