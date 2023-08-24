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
package com.certapp.service;

import com.certapp.common.code.ConstantCode;
import com.certapp.common.enums.CertState;
import com.certapp.common.exception.BaseException;
import com.certapp.common.properties.ConstantProperties;
import com.certapp.common.utils.*;
import com.certapp.dao.entity.*;
import com.certapp.dao.mapper.*;
import com.certapp.entity.*;
import com.certapp.entity.request.CertConfigReq;
import com.certapp.entity.request.QueryResultPageReq;
import com.certapp.entity.response.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.webank.wbbc.dappsdk.WBBCClient;
import com.webank.wbbc.dappsdk.model.request.webaseTranx.ReqSendNew;
import com.webank.wbbc.dappsdk.model.response.webaseTranx.RespSend;
import com.webank.wbbc.dappsdk.utils.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class CertificateService {

    @Autowired
    private TbCertConfigMapper tbCertConfigMapper;
    @Autowired
    private TbFiledInfoMapper tbFiledInfoMapper;
    @Autowired
    private ConstantProperties constantProperties;
    @Autowired
    private TbQueryPageMapper tbQueryPageMapper;
    @Autowired
    private TbQueryResultPageMapper tbQueryResultPageMapper;
    @Autowired
    private TbCertInfoMapper tbCertInfoMapper;
    @Autowired
    private WBBCClient client;

    private static CryptoSuite cryptoSuite = new CryptoSuite(0);
    public final static String FILE_QR = "/qr/%s_qr.png";
    public final static String FILE_TEMPLATE = "/template_%s.png";
    public final static String FILE_CERT_CREATE = "/create/%s.png";
    public final static String FILE_FORMAT = "data:image/png;base64,";


    @Transactional
    public CertConfigResp saveCertPic(CertConfigReq req) {
        CertConfigResp certConfigResp = new CertConfigResp();
        List<FiledInfoVo> filedInfoList = new ArrayList<>();
        List<Integer> filedIdList = new ArrayList<>();
        AtomicInteger i = new AtomicInteger(0);
        //补充字段序号
        req.getFields().stream().forEach(filed -> {
            filed.setFiledSerial(String.valueOf(i.get()));
            i.getAndIncrement();
        });
        if (req.getId() != null) {
            //根据证书名称查询出来的合约id不匹配则提示名称重复
            List<TbCertConfig> list = tbCertConfigMapper.selectByCertName(req.getCertName());
            if (CollectionUtils.isNotEmpty(list)) {
                list.stream().forEach(cert -> {
                    if (cert.getId() != req.getId()) {
                        log.error("cert name has bean in use.");
                        throw new BaseException(ConstantCode.CERT_NAME_THE_SAME);
                    }
                });
            }
            TbCertConfig oldCert = tbCertConfigMapper.selectByPrimaryKey(req.getId());
            //更新证书图片配置表
            TbCertConfig tbCertConfig = new TbCertConfig();
            tbCertConfig.setId(req.getId());
            tbCertConfig.setQrCodeUrl(req.getQrCodeUrl());
            tbCertConfig.setCertName(req.getCertName());
            //后台生成图片不需要前缀
            String basePic = req.getBaseMap().replace(FILE_FORMAT, "");
            req.setBaseMap(basePic);
            //数据库保存带前缀的数据
            tbCertConfig.setBaseMap(FILE_FORMAT + basePic);
            tbCertConfig.setCertDesc(req.getCertDesc());
            tbCertConfig.setFiledFont(req.getFiledFont());
            tbCertConfig.setNumFont(req.getNumFont());
            tbCertConfig.setQrCodeLocal(JsonUtils.objToString(req.getQrCodeLocal()));
            tbCertConfig.setModifyTime(new Date());
            tbCertConfig.setCreateTime(oldCert.getCreateTime());
            tbCertConfig.setCertReal(0);
            int res = tbCertConfigMapper.updateByPrimaryKey(tbCertConfig);
            log.info("saveCertPic. updateByPrimaryKey :{}", res);
            //更新字段表(先删除原来的字段，再新增)
            int del = tbFiledInfoMapper.deleteByCertConfigId(req.getId());
            log.info("saveCertPic. deleteByCertConfigId :{}", del);
            req.getFields().stream().forEach(filedInfoVo -> {
                TbFiledInfo tbFiledInfo = new TbFiledInfo();
                FiledLocal filedLocal = new FiledLocal();
                filedLocal.setFiledLocal(filedInfoVo.getX(), filedInfoVo.getY(), filedInfoVo.getW(), filedInfoVo.getH());
                tbFiledInfo.setCertConfigId(tbCertConfig.getId());
                tbFiledInfo.setFiledDemo(filedInfoVo.getFiledDemo());
                tbFiledInfo.setFiledLocal(JsonUtils.objToString(filedLocal));
                tbFiledInfo.setFiledName(filedInfoVo.getFiledName());
                tbFiledInfo.setFiledSerial(filedInfoVo.getFiledSerial());
                tbFiledInfo.setFiledNameZh(filedInfoVo.getFiledNameZh());
                tbFiledInfo.setShowEnable(filedInfoVo.getShowEnable());
                tbFiledInfoMapper.insertSelective(tbFiledInfo);
                filedIdList.add(tbFiledInfo.getId());
            });
            log.info("saveCertPic. filedIdList :{}", JacksonUtils.toJSONString(filedIdList));
            TbCertConfig certConfig = tbCertConfigMapper.selectByPrimaryKey(req.getId());
            BeanUtils.copyProperties(certConfig, certConfigResp);
            //二维码位置
            QrCodeLocal qrCodeLocal = JsonUtils.stringToObj(certConfig.getQrCodeLocal(), QrCodeLocal.class);
            certConfigResp.setQrCodeLocal(qrCodeLocal);
            //不返回预览和底图
//            certConfigResp.setPicPreview(null);
//            certConfigResp.setBaseMap(null);
            req.setId(tbCertConfig.getId());
            certConfigResp = qrPicFiled(tbCertConfig, certConfigResp, req, filedIdList, filedInfoList);
            return certConfigResp;
        } else {
            //insert
            List<TbCertConfig> list = tbCertConfigMapper.selectByCertName(req.getCertName());
            //判断名称是否重复
            if (CollectionUtils.isNotEmpty(list)) {
                log.error("cert name has bean in use.");
                throw new BaseException(ConstantCode.CERT_NAME_THE_SAME);
            }
            TbCertConfig tbCertConfig = new TbCertConfig();
            tbCertConfig.setQrCodeUrl(req.getQrCodeUrl());
            tbCertConfig.setCertName(req.getCertName());
            //后台生成图片不需要前缀
            String basePic = req.getBaseMap().replace(FILE_FORMAT, "");
            req.setBaseMap(basePic);
            //数据库保存带前缀的数据
            tbCertConfig.setBaseMap(FILE_FORMAT + basePic);
            tbCertConfig.setCertDesc(req.getCertDesc());
            tbCertConfig.setFiledFont(req.getFiledFont());
            tbCertConfig.setNumFont(req.getNumFont());
            tbCertConfig.setQrCodeLocal(JsonUtils.objToString(req.getQrCodeLocal()));
            tbCertConfig.setCreateTime(new Date());
            tbCertConfig.setModifyTime(new Date());
            int row = tbCertConfigMapper.insertSelective(tbCertConfig);
            log.info("saveCertPic. insertSelective :{}", row);
            if (row != 0) {
                req.getFields().stream().forEach(filedInfoVo -> {
                    TbFiledInfo tbFiledInfo = new TbFiledInfo();
                    FiledLocal filedLocal = new FiledLocal();
                    filedLocal.setFiledLocal(filedInfoVo.getX(), filedInfoVo.getY(), filedInfoVo.getW(), filedInfoVo.getH());
                    tbFiledInfo.setCertConfigId(tbCertConfig.getId());
                    tbFiledInfo.setFiledDemo(filedInfoVo.getFiledDemo());
                    tbFiledInfo.setFiledLocal(JsonUtils.objToString(filedLocal));
                    tbFiledInfo.setFiledName(filedInfoVo.getFiledName());
                    tbFiledInfo.setFiledSerial(filedInfoVo.getFiledSerial());
                    tbFiledInfo.setFiledNameZh(filedInfoVo.getFiledNameZh());
                    tbFiledInfo.setShowEnable(filedInfoVo.getShowEnable());
                    tbFiledInfoMapper.insertSelective(tbFiledInfo);
                    filedIdList.add(tbFiledInfo.getId());
                });
            }
            log.info("saveCertPic. filedIdList :{}", JacksonUtils.toJSONString(filedIdList));
            TbCertConfig certConfig = tbCertConfigMapper.selectByPrimaryKey(tbCertConfig.getId());
            BeanUtils.copyProperties(certConfig, certConfigResp);
            //二维码位置
            QrCodeLocal qrCodeLocal = JsonUtils.stringToObj(certConfig.getQrCodeLocal(), QrCodeLocal.class);
            certConfigResp.setQrCodeLocal(qrCodeLocal);
            req.setId(tbCertConfig.getId());
            certConfigResp = qrPicFiled(tbCertConfig, certConfigResp, req, filedIdList, filedInfoList);

        }
        return certConfigResp;
    }

    private CertConfigResp qrPicFiled(TbCertConfig tbCertConfig, CertConfigResp certConfigResp, CertConfigReq req, List<Integer> filedIdList, List<FiledInfoVo> filedInfoList) {

        // 生成二维码
        String qrContent = String.format(tbCertConfig.getQrCodeUrl(), tbCertConfig.getId());
        String qrFilePath = constantProperties.getFilePath() + String.format(FILE_QR, tbCertConfig.getId());
        try {
            QrCodeUtil.save(qrContent, null, qrFilePath);
        } catch (Exception e) {
            log.error("create qrCode error.", e);
            throw new BaseException(ConstantCode.CREATE_QRCODE_ERROR);
        }
        //保存底图到本地
        boolean b = XmlInterfaceUtils.generateBase64StringToFile(req.getBaseMap(), constantProperties.getFilePath() + String.format(FILE_TEMPLATE, tbCertConfig.getId()));
        if (!b) {
            log.error("create cert pic error. fileFilePath:{}", constantProperties.getFilePath() + String.format(FILE_TEMPLATE, tbCertConfig.getId()));
            throw new BaseException(ConstantCode.CREATE_CERTPIC_ERROR);
        }
        // 合成图片
        String certPath = constantProperties.getFilePath() + String.format(FILE_CERT_CREATE, tbCertConfig.getId());
        try {
            bigImgAddSmallImgAndText(constantProperties.getFilePath() + String.format(FILE_TEMPLATE, tbCertConfig.getId()), qrFilePath,
                req, certPath);
        } catch (Exception e) {
            log.error("create cert pic error.", e);
            throw new BaseException(ConstantCode.CREATE_CERT_ERROR);
        }

        //图片转base64
        try {
            String picBase64 = XmlInterfaceUtils.fileToBase64(certPath);
            String picPreview = FILE_FORMAT + picBase64;
            int row = tbCertConfigMapper.updatePicById(tbCertConfig.getId(), picPreview);
            log.info("qrPicFiled. updatePicById :{}", row);
            //返回预览图
            certConfigResp.setPicPreview(picPreview);
        } catch (Exception e) {
            log.error("update pic preview error.", e);
            throw new BaseException(ConstantCode.UPDATE_PIC_PREVIEW_ERROR);
        }

        if (CollectionUtils.isNotEmpty(filedIdList)) {
            filedIdList.stream().forEach(id -> {
                TbFiledInfo filed = tbFiledInfoMapper.selectByPrimaryKey(id);
                FiledInfoVo filedInfoVo = new FiledInfoVo();
                BeanUtils.copyProperties(filed, filedInfoVo);
                FiledLocal filedLocal = JsonUtils.stringToObj(filed.getFiledLocal(), FiledLocal.class);
                filedInfoVo.setX(filedLocal.getX());
                filedInfoVo.setY(filedLocal.getY());
                filedInfoVo.setW(filedLocal.getW());
                filedInfoVo.setH(filedLocal.getH());
                filedInfoList.add(filedInfoVo);
            });
        }
        certConfigResp.setFields(filedInfoList);
        return certConfigResp;
    }

    /**
     * @param bigImgPath   底图路径
     * @param smallImgPath 二维码路径
     * @param req          填充的数据
     * @param outPath      生成证书路径
     * @throws IOException
     */
    private static void bigImgAddSmallImgAndText(String bigImgPath, String smallImgPath,
                                                 CertConfigReq req, String outPath) throws IOException {
        log.info("bigImgAddSmallImgAndText. bigImgPath:{} smallImgPath:{} outPath:{}", bigImgPath,
            smallImgPath, outPath);
        BufferedImage targetImg = ImageIO.read(new File(bigImgPath));
        int imgWidth = targetImg.getWidth();
        int imgHeight = targetImg.getHeight();
        BufferedImage bufferedImage =
            new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(targetImg, 0, 0, imgWidth, imgHeight, null);
        g.setColor(Color.BLACK);
        if (req != null) {
            // 设置文本字体，居中显示
            Font font = new Font("宋体", Font.PLAIN, Integer.parseInt(req.getFiledFont()));
            g.setFont(font);
            for (int i = 0; i < req.getFields().size(); i++) {
                for (FiledInfoVo filedInfoVo : req.getFields()) {
                    if (filedInfoVo.getShowEnable() == 1) {
                        if (filedInfoVo.getFiledSerial().equals(String.valueOf(i))) {
                            int[] contentSize = getContentSize(font, filedInfoVo.getFiledDemo());
                            g.drawString(filedInfoVo.getFiledDemo(), filedInfoVo.getX() + filedInfoVo.getW() / 2 - contentSize[0] / 2,
                                filedInfoVo.getY() + contentSize[1]);
                        }
                    }
                }
            }
//            g.drawString(String.valueOf(req.getId()), req.getNumberLocal().getX(), req.getNumberLocal().getY()); // 设置证书编号
        }
        // 添加二维码图片
        BufferedImage icon = ImageIO.read(new File(smallImgPath));
        g.drawImage(icon, req.getQrCodeLocal().getX(), req.getQrCodeLocal().getY(), req.getQrCodeLocal().getW(), req.getQrCodeLocal().getH(), null);
        FileOutputStream outImgStream = new FileOutputStream(outPath);
        ImageIO.write(bufferedImage, "png", outImgStream);
        g.dispose();
        outImgStream.close();
    }

    /**
     * 获取文本的长度，字体大小不同，长度也不同
     *
     * @param font
     * @param content
     * @return
     */
    private static int[] getContentSize(Font font, String content) {
        int[] contentSize = new int[2];
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        Rectangle rect = font.getStringBounds(content, frc).getBounds();
        contentSize[0] = (int) rect.getWidth();
        contentSize[1] = (int) rect.getHeight();
        return contentSize;
    }

    public CertConfigResp queryCertPic(Integer id) {
        CertConfigResp certConfigResp = new CertConfigResp();
        List<FiledInfoVo> filedInfoList = new ArrayList<>();
        TbCertConfig certConfig = tbCertConfigMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(certConfig, certConfigResp);
        //获取填充字段数据
        List<TbFiledInfo> tbFiledInfoList = tbFiledInfoMapper.selectByCertConfigId(id);
        if (CollectionUtils.isNotEmpty(tbFiledInfoList)) {
            tbFiledInfoList.stream().forEach(tbFiledInfo -> {
                FiledInfoVo filedInfoVo = new FiledInfoVo();
                BeanUtils.copyProperties(tbFiledInfo, filedInfoVo);
                FiledLocal filedLocal = JsonUtils.stringToObj(tbFiledInfo.getFiledLocal(), FiledLocal.class);
                filedInfoVo.setX(filedLocal.getX());
                filedInfoVo.setY(filedLocal.getY());
                filedInfoVo.setW(filedLocal.getW());
                filedInfoVo.setH(filedLocal.getH());
                filedInfoList.add(filedInfoVo);
            });
        }
        log.info("queryCertPic. filedInfoList :{}", JacksonUtils.toJSONString(filedInfoList));
        QrCodeLocal qrCodeLocal = JsonUtils.stringToObj(certConfig.getQrCodeLocal(), QrCodeLocal.class);
        //二维码位置
        certConfigResp.setQrCodeLocal(qrCodeLocal);
        certConfigResp.setFields(filedInfoList);
        return certConfigResp;
    }

    public String previewCertPic(Integer id) {
        TbCertConfig certConfig = tbCertConfigMapper.selectByPrimaryKey(id);
        //不为空返回模板预览图
        if (certConfig != null) {
            return certConfig.getPicPreview();
        }
        return null;
    }

    public List<CertResp> getCertList() {
        List<TbCertConfig> tbCertAllList = tbCertConfigMapper.selectAllCert();
        if (CollectionUtils.isEmpty(tbCertAllList)) {
            return Collections.emptyList();
        }
        List<CertResp> certRespList = new ArrayList<>();
        tbCertAllList.stream().forEach(tbCertConfig -> {
            log.info("getCertList. CertConfig id :{}", tbCertConfig.getId());
            CertResp certResp = new CertResp();
            List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectByCertConfigId(tbCertConfig.getId());
            //0没数据，1有证书图片，返给前端判断
            certResp.setIsCertPic(CertState.revocation.getType());
            if (CollectionUtils.isEmpty(tbCertInfoList)) {
                certResp.setCertState(CertState.revocation.getType());
            } else {
                certResp.setCertState(tbCertInfoList.get(0).getCertState());
                if (StringUtils.isNotEmpty(tbCertInfoList.get(0).getCertPic())) {
                    certResp.setIsCertPic(CertState.normal.getType());
                }
            }
            certResp.setCertName(tbCertConfig.getCertName());
            certResp.setCertConfigId(tbCertConfig.getId());
            //是否为固定模板（1为固定模板，不能删除）
            if (tbCertConfig.getCertReal() == 0) {
                certResp.setCertReal(true);
            } else {
                certResp.setCertReal(false);
            }

            certRespList.add(certResp);
        });
        return certRespList;
    }

    public QueryPage saveQueryPage(QueryPage req) {
        TbQueryPage tbQueryPage = tbQueryPageMapper.selectByCertConfigId(req.getCertConfigId());
        QueryPage queryPage = new QueryPage();
        if (tbQueryPage == null) {
            //insert
            tbQueryPage = new TbQueryPage();
            BeanUtils.copyProperties(req, tbQueryPage);
            int row = tbQueryPageMapper.insertSelective(tbQueryPage);
            log.info("saveQueryPage. insertSelective :{}", row);
            TbQueryPage tbQp = tbQueryPageMapper.selectByPrimaryKey(tbQueryPage.getId());
            BeanUtils.copyProperties(tbQp, queryPage);
        } else {
            //update
            TbQueryPage tbQueryPageUp = new TbQueryPage();
            BeanUtils.copyProperties(req, tbQueryPageUp);
            int row = tbQueryPageMapper.updateByCertConfigId(tbQueryPageUp);
            log.info("saveQueryPage. updateByCertConfigId :{}", row);
            TbQueryPage tbQp = tbQueryPageMapper.selectByCertConfigId(req.getCertConfigId());
            BeanUtils.copyProperties(tbQp, queryPage);
        }
        return queryPage;
    }

    public QueryResultPageResp saveQueryResultPage(QueryResultPageReq req) {
        TbQueryResultPage tbQueryResultPage = tbQueryResultPageMapper.selectByCertConfigId(req.getCertConfigId());
        QueryResultPageResp queryResultPageResp = new QueryResultPageResp();
        if (tbQueryResultPage == null) {
            //insert
            tbQueryResultPage = new TbQueryResultPage();
            BeanUtils.copyProperties(req, tbQueryResultPage);
            //显示字段
            tbQueryResultPage.setEnableFields(req.getEnableFields().toString());
            int row = tbQueryResultPageMapper.insertSelective(tbQueryResultPage);
            log.info("saveQueryResultPage. insertSelective :{}", row);
            TbQueryResultPage tqrp = tbQueryResultPageMapper.selectByPrimaryKey(tbQueryResultPage.getId());
            BeanUtils.copyProperties(tqrp, queryResultPageResp);
        } else {
            //update
            TbQueryResultPage tbQueryResultPageUp = new TbQueryResultPage();
            BeanUtils.copyProperties(req, tbQueryResultPageUp);
            //显示字段
            tbQueryResultPageUp.setEnableFields(req.getEnableFields().toString());
            int row = tbQueryResultPageMapper.updateByCertConfigId(tbQueryResultPageUp);
            log.info("saveQueryResultPage. updateByCertConfigId :{}", row);
            TbQueryResultPage tqrp = tbQueryResultPageMapper.selectByCertConfigId(req.getCertConfigId());
            BeanUtils.copyProperties(tqrp, queryResultPageResp);
        }
        return queryResultPageResp;
    }

    public QueryPage getQueryPage(Integer certConfigId) {
        TbQueryPage tbQueryPage = tbQueryPageMapper.selectByCertConfigId(certConfigId);
        log.info("getQueryPage. selectByCertConfigId :{}", JacksonUtils.toJSONString(tbQueryPage));
        if (tbQueryPage == null) {
            return null;
        }
        QueryPage queryPage = new QueryPage();
        BeanUtils.copyProperties(tbQueryPage, queryPage);
        List<TbFiledInfo> tbFiledInfoList = tbFiledInfoMapper.selectByCertConfigId(tbQueryPage.getCertConfigId());
        if (CollectionUtils.isNotEmpty(tbFiledInfoList)) {
            queryPage.setFields(tbFiledInfoList);
        }
        return queryPage;
    }

    public QueryResultPage getQueryResultPage(Integer certConfigId) {
        QueryResultPage queryResultPage = new QueryResultPage();
        TbQueryResultPage tbQueryResultPage = tbQueryResultPageMapper.selectByCertConfigId(certConfigId);
        List<TbFiledInfo> tbFiledInfoList = tbFiledInfoMapper.selectByCertConfigId(certConfigId);
        TbCertConfig tbCertConfig = tbCertConfigMapper.selectByPrimaryKey(certConfigId);
        List<FiledInfo> filedInfoList = new ArrayList<>();
        tbFiledInfoList.stream().forEach(tbFiledInfo -> {
            FiledInfo filedInfo = new FiledInfo();
            BeanUtils.copyProperties(tbFiledInfo, filedInfo);
            filedInfoList.add(filedInfo);
        });
        //补充字段
        FiledInfo hash = new FiledInfo();
        hash.setCertConfigId(certConfigId);
        hash.setFiledName("certHash");
        hash.setFiledNameZh("上链哈希");
        hash.setShowEnable(CertState.normal.getType());
        hash.setFiledSerial(String.valueOf(tbFiledInfoList.size()));
        filedInfoList.add(hash);
        FiledInfo certTime = new FiledInfo();
        certTime.setCertConfigId(certConfigId);
        certTime.setFiledName("certTime");
        certTime.setFiledNameZh("获取时间");
        certTime.setShowEnable(CertState.normal.getType());
        certTime.setFiledSerial(String.valueOf(tbFiledInfoList.size() + 1));
        FiledInfo certOrg = new FiledInfo();
        certOrg.setCertConfigId(certConfigId);
        certOrg.setFiledName("certOrg");
        certOrg.setFiledNameZh("发证机构");
        certOrg.setShowEnable(CertState.normal.getType());
        certTime.setFiledSerial(String.valueOf(tbFiledInfoList.size() + 2));
        filedInfoList.add(certOrg);
        FiledInfo certName = new FiledInfo();
        certName.setCertConfigId(certConfigId);
        certName.setFiledName("certName");
        certName.setFiledNameZh("证书名称");
        certName.setShowEnable(CertState.normal.getType());
        certTime.setFiledSerial(String.valueOf(tbFiledInfoList.size() + 3));
        filedInfoList.add(certName);
        filedInfoList.add(certTime);
        queryResultPage.setFields(filedInfoList);
        if (tbQueryResultPage == null) {
            //为空，返回图片配置表的字段数据
            return queryResultPage;
        }
        //不为空，返回图片配置表的字段数据和查询结果页表的字段数据
        BeanUtils.copyProperties(tbQueryResultPage, queryResultPage);
        List<FiledInfo> fields = JsonUtils.stringToObj(JsonUtils.objToString(tbQueryResultPage.getEnableFields()),
            new TypeReference<List<FiledInfo>>() {
            });
        List<String> enableFields = new ArrayList<>();
        fields.stream().forEach(enableField -> {
            enableFields.add(JsonUtils.objToString(enableField));
        });
        queryResultPage.setEnableFields(enableFields);
        queryResultPage.setPicPreview(tbCertConfig.getPicPreview());
        return queryResultPage;
    }

    @Transactional
    public void importData(Integer certConfigId, MultipartFile file) {
        //先删除原有的数据
        int row = tbCertInfoMapper.deleteByCertConfigId(certConfigId);
        log.info("importData deleteByCertConfigId:{}", row);
        String fileName = UUID.randomUUID().toString().replace("-", "");
        try {
            //拿到文件并上传到本地
            FileUploadUtil.fileUpload(file, constantProperties.getFilePath(), fileName);
        } catch (Exception e) {
            log.error("file upload error.", e);
            throw new BaseException(ConstantCode.FILE_UPLOAD_ERROR);
        }
        try {
            //读excel并存储DB
            readExcel(certConfigId, fileName);
        } catch (Exception e) {
            log.error("read excel error.", e);
            throw new BaseException(ConstantCode.READ_EXCEL_ERROR);
        }
        try {
            //生成证书图片并更新DB
            createCert(certConfigId);
        } catch (Exception e) {
            log.error("create cert error.", e);
            throw new BaseException(ConstantCode.CREATE_CERT_ERROR);
        }
    }

    public void createCert(Integer certConfigId) {
        //获取证书配置信息
        TbCertConfig tbCertConfig = tbCertConfigMapper.selectByPrimaryKey(certConfigId);
        CertConfigReq certConfig = new CertConfigReq();
        BeanUtils.copyProperties(tbCertConfig, certConfig);
        //设置二维码位置
        QrCodeLocal qrCodeLocal = JsonUtils.stringToObj(tbCertConfig.getQrCodeLocal(), QrCodeLocal.class);
        certConfig.setQrCodeLocal(qrCodeLocal);
        //获取字段信息
        List<TbFiledInfo> tbFiledInfoList = tbFiledInfoMapper.selectByCertConfigId(certConfigId);
        //获取导入字段数据
        List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectByCertConfigId(certConfigId);


        // 生成二维码
        String qrContent = String.format(tbCertConfig.getQrCodeUrl(), tbCertConfig.getId());
        String qrFilePath = constantProperties.getFilePath() + String.format(FILE_QR, tbCertConfig.getId());
        try {
            QrCodeUtil.save(qrContent, null, qrFilePath);
        } catch (Exception e) {
            log.error("create qrCode error.", e);
            throw new BaseException(ConstantCode.CREATE_QRCODE_ERROR);
        }
        String basePic = certConfig.getBaseMap().replace(FILE_FORMAT, "");
        tbCertConfig.setBaseMap(basePic);

        //保存底图到本地
        boolean b = XmlInterfaceUtils.generateBase64StringToFile(tbCertConfig.getBaseMap(), constantProperties.getFilePath() + String.format(FILE_TEMPLATE, tbCertConfig.getId()));
        if (!b) {
            log.error("create cert pic error. fileFilePath:{}", constantProperties.getFilePath() + String.format(FILE_TEMPLATE, tbCertConfig.getId()));
            throw new BaseException(ConstantCode.CREATE_CERTPIC_ERROR);
        }

        TbQueryPage tbQueryPage = tbQueryPageMapper.selectByCertConfigId(certConfigId);

        for (int i = 0; i < tbCertInfoList.size(); i++) {
            List<FiledInfoVo> filedInfoList = new ArrayList<>();
            String[] info = tbCertInfoList.get(i).getFiledInfo().split(";");
            if (CollectionUtils.isNotEmpty(tbFiledInfoList)) {
                tbFiledInfoList.stream().forEach(tbFiledInfo -> {
                    FiledInfoVo filedInfoVo = new FiledInfoVo();
                    BeanUtils.copyProperties(tbFiledInfo, filedInfoVo);
                    //根据序号获取导入的数据
                    filedInfoVo.setFiledDemo(info[Integer.parseInt(filedInfoVo.getFiledSerial())]);
                    FiledLocal filedLocal = JsonUtils.stringToObj(tbFiledInfo.getFiledLocal(), FiledLocal.class);
                    filedInfoVo.setX(filedLocal.getX());
                    filedInfoVo.setY(filedLocal.getY());
                    filedInfoVo.setW(filedLocal.getW());
                    filedInfoVo.setH(filedLocal.getH());
                    filedInfoList.add(filedInfoVo);
                });
            }
            certConfig.setFields(filedInfoList);
            String picName = UUID.randomUUID().toString().replace("-", "");
            // 合成图片
            String certPath = constantProperties.getFilePath() + String.format(FILE_CERT_CREATE, picName);
            try {
                bigImgAddSmallImgAndText(constantProperties.getFilePath() + String.format(FILE_TEMPLATE, tbCertConfig.getId()), qrFilePath,
                    certConfig, certPath);
            } catch (Exception e) {
                log.error("create cert pic error.", e);
                throw new BaseException(ConstantCode.CREATE_CERT_ERROR);
            }

            //保存搜索字段数据
            String searchFiled = info[Integer.parseInt(tbQueryPage.getFiledSerial())];
            tbCertInfoMapper.updateSearchFiledById(tbCertInfoList.get(i).getId(), searchFiled);

            //图片转base64
            try {
                String picBase64 = XmlInterfaceUtils.fileToBase64(certPath);
                String certPic = FILE_FORMAT + picBase64;
                tbCertInfoMapper.updateCertPicById(tbCertInfoList.get(i).getId(), certPic);
            } catch (Exception e) {
                log.error("update pic preview error.", e);
                throw new BaseException(ConstantCode.UPDATE_PIC_PREVIEW_ERROR);
            }
        }
    }


    public void readExcel(Integer certConfigId, String fileName) throws IOException {
        String filePath = constantProperties.getFilePath() + "/" + fileName + ".xlsx";
        log.info("readExcel filePath: {}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BaseException(ConstantCode.FILE_NOT_EXISTS);
        }
        InputStream in = new FileInputStream(file);
        // 读取整个Excel
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        // 获取第一个表单Sheet
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        // 默认第一行为标题行
        int i = 1;
        while (sheetAt.getRow(i) != null) {
            XSSFRow row = sheetAt.getRow(i);
            CertInfo certInfo = new CertInfo();
            if (StringUtils.isBlank(row.getCell(0).toString())) {
                break;
            }
            //获取最大列数
            int columnNum = row.getPhysicalNumberOfCells();
            certInfo.setCertConfigId(certConfigId);
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < columnNum; j++) {
                sb.append(StringUtils.isNotEmpty(row.getCell(j).toString()) ? row.getCell(j).toString() + ";" : null + ";");
            }
            sb.deleteCharAt(sb.length() - 1);
            certInfo.setFiledInfo(sb.toString());
            // 计算hash
            String certHash = getHash(certInfo.toString());
            certInfo.setCertHash(certHash);
            log.info("readExcel certInfo:{}", JacksonUtils.toJSONString(certInfo));
            // 存储DB
            TbCertInfo tbCertInfo = new TbCertInfo();
            BeanUtils.copyProperties(certInfo, tbCertInfo);
            tbCertInfoMapper.insertSelective(tbCertInfo);
            // 设置证书编号（md5）
            String certId = CommonUtils.md5Encrypt(tbCertInfo.getId().toString());
            tbCertInfoMapper.updateCertIdById(tbCertInfo.getId(), certId);
            i += 1;
        }
        in.close();
        sheets.close();
    }

    private String getHash(String text) {
        return Hex.toHexStringWithPrefix(cryptoSuite.hash(text.getBytes()));
    }

    public List<FiledInfoVo> queryFiledByCertId(Integer certConfigId) {
        List<TbFiledInfo> tbFiledInfoList = tbFiledInfoMapper.selectByCertConfigId(certConfigId);
        List<FiledInfoVo> filedInfoVoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tbFiledInfoList)) {
            tbFiledInfoList.stream().forEach(tbFiledInfo -> {
                FiledInfoVo filedInfoVo = new FiledInfoVo();
                BeanUtils.copyProperties(tbFiledInfo, filedInfoVo);
                FiledLocal filedLocal = JsonUtils.stringToObj(tbFiledInfo.getFiledLocal(), FiledLocal.class);
                filedInfoVo.setX(filedLocal.getX());
                filedInfoVo.setY(filedLocal.getY());
                filedInfoVo.setW(filedLocal.getW());
                filedInfoVo.setH(filedLocal.getH());
                filedInfoVoList.add(filedInfoVo);
            });
        }
        return filedInfoVoList;
    }

    public void cochain(Integer certConfigId, String bizSeqNo) {
        List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectByCertConfigId(certConfigId);
        if (CollectionUtils.isNotEmpty(tbCertInfoList)) {
            tbCertInfoList.stream().forEach(tbCertInfo -> {
                ReqSendNew send = new ReqSendNew();
                send.setFuncName("create");
                List<String> funcParam = new ArrayList<>();
                //证书id + 证书hash上链
                funcParam.add(tbCertInfo.getCertHash());
                funcParam.add(tbCertInfo.getCertId());
                send.setFuncParam(funcParam);
                List<Object> functionAbi = GsonUtil.toList("[{\"constant\":false,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"h\",\"type\":\"string\"}],\"name\":\"create\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"addIssuer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"s\",\"type\":\"uint256\"}],\"name\":\"updateStatus\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"h\",\"type\":\"string\"}],\"name\":\"updateHash\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"isIssuer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"renounceIssuer\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"}],\"name\":\"queryById\",\"outputs\":[{\"name\":\"vhash\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"o\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"id\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"h\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"Newcontract\",\"type\":\"address\"}],\"name\":\"create_LOG\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"id\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"h\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"Newcontract\",\"type\":\"address\"}],\"name\":\"updateHash_LOG\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"id\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"s\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"Newcontract\",\"type\":\"address\"}],\"name\":\"updateStatus_LOG\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"IssuerAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"account\",\"type\":\"address\"}],\"name\":\"IssuerRemoved\",\"type\":\"event\"}]");
                send.setFunctionAbi(functionAbi);
                send.setSignUserId(constantProperties.getSignUserId());
                RespSend respSend = client.sendNew(send, bizSeqNo);
                if (respSend == null) {
                    throw new BaseException(ConstantCode.CERT_COCHAIN_FAIL);
                }
                int certState = CertState.normal.getType();
                Date certTime = new Date();
                int row = tbCertInfoMapper.updateTxHashById(tbCertInfo.getId(), respSend.getTransactionHash(), certState, certTime);
                log.info("cochain updateTxHashById:{}", row);
            });
        }
    }


    @Transactional
    public void deleteById(Integer id) {
        List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectByCertConfigId(id);
        if (CollectionUtils.isNotEmpty(tbCertInfoList)) {
            //已上链或发布的证书不能删除
            if (tbCertInfoList.get(0).getCertState() == CertState.normal.getType() || tbCertInfoList.get(0).getCertState() == CertState.publish.getType()) {
                throw new BaseException(ConstantCode.CERT_DELETE_ERROR);
            }
        }
        //删除配置证书图片
        int certConfig = tbCertConfigMapper.deleteByPrimaryKey(id);
        log.info("delete certConfig:{}", certConfig);
        //删除证书信息
        int certInfo = tbCertInfoMapper.deleteByCertConfigId(id);
        log.info("delete certInfo:{}", certInfo);
        //删除证书字段
        int filedInfo = tbFiledInfoMapper.deleteByCertConfigId(id);
        log.info("delete filedInfo:{}", filedInfo);
        //删除查询页
        int queryPage = tbQueryPageMapper.deleteByCertConfigId(id);
        log.info("delete queryPage:{}", queryPage);
        //删除查询结果页
        int queryResultPage = tbQueryResultPageMapper.deleteByCertConfigId(id);
        log.info("delete queryResultPage:{}", queryResultPage);
    }

    public CertFiledInfoResp getFiledInfo(Integer certConfigId) {
        List<TbFiledInfo> tbFiledInfoList = tbFiledInfoMapper.selectByCertConfigId(certConfigId);
        log.info("getFiledInfo tbFiledInfoList:{}", JacksonUtils.toJSONString(tbFiledInfoList));
        List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectByCertConfigId(certConfigId);
        log.info("getFiledInfo tbCertInfoList:{}", JacksonUtils.toJSONString(tbCertInfoList));
        CertFiledInfoResp certFiledInfoResp = new CertFiledInfoResp();
        List<CertFiledInfo> certFiledInfoList = new ArrayList<>();
        List<FiledInfoResp> filedInfoRespList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tbFiledInfoList)) {
            tbFiledInfoList.stream().forEach(tbFiledInfo -> {
                CertFiledInfo certFiledInfo = new CertFiledInfo();
                certFiledInfo.setFiledName(tbFiledInfo.getFiledName());
                certFiledInfo.setFiledNameZh(tbFiledInfo.getFiledNameZh());
                certFiledInfo.setFiledSerial(tbFiledInfo.getFiledSerial());
                certFiledInfoList.add(certFiledInfo);
            });
        }
        if (CollectionUtils.isNotEmpty(tbCertInfoList)) {
            tbCertInfoList.stream().forEach(tbCertInfo -> {
                FiledInfoResp filedInfoResp = new FiledInfoResp();
                filedInfoResp.setFiledInfo(tbCertInfo.getFiledInfo());
                filedInfoResp.setId(tbCertInfo.getId());
                filedInfoRespList.add(filedInfoResp);
            });
        }
        certFiledInfoResp.setCertState(tbCertInfoList.get(0).getCertState());
        certFiledInfoResp.setCertFiledInfo(certFiledInfoList);
        certFiledInfoResp.setFiledInfoResp(filedInfoRespList);
        return certFiledInfoResp;
    }

    public String downloadById(Integer id) {
        //根据证书id返回单个证书图片
        TbCertInfo tbCertInfo = tbCertInfoMapper.selectByPrimaryKey(id);
        return tbCertInfo.getCertPic();
    }

    public List<CertPicResp> download(Integer certConfigId) {
        //根据证书配置id返回所有证书图片
        List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectByCertConfigId(certConfigId);
        List<CertPicResp> picList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tbCertInfoList)) {
            tbCertInfoList.stream().forEach(tbCertInfo -> {
                CertPicResp certPicResp = new CertPicResp();
                certPicResp.setCertPic(tbCertInfo.getCertPic());
                certPicResp.setId(tbCertInfo.getId());
                picList.add(certPicResp);
            });
        }
        return picList;
    }

    public void updateCertState(Integer certConfigId, boolean state) {
        List<TbCertInfo> tbCertInfoList = tbCertInfoMapper.selectByCertConfigId(certConfigId);
        if (CollectionUtils.isNotEmpty(tbCertInfoList)) {
            tbCertInfoList.stream().forEach(tbCertInfo -> {
                if (tbCertInfo.getCertState() == 0) {
                    log.error("update cert state error. certId:{}", tbCertInfo.getCertId());
                    throw new BaseException(ConstantCode.UPDATE_CERT_STATE_ERROR);
                }
            });
        }
        int certState;
        if (state) {
            //2发布状态
            certState = CertState.publish.getType();
        } else {
            //1上链状态
            certState = CertState.normal.getType();
        }
        tbCertInfoMapper.updateCertStateByConfigId(certConfigId, certState);
    }

    @Transactional
    public int copeCert(Integer certConfigId) {
        TbCertConfig tbCertConfig = tbCertConfigMapper.selectByPrimaryKey(certConfigId);
        if (tbCertConfig == null) {
            log.error("update cert state error. certConfigId:{}", certConfigId);
            throw new BaseException(ConstantCode.CERT_NONE_EXIST);
        }
        //复制证书配置表
        TbCertConfig newCertConfig = new TbCertConfig();
        BeanUtils.copyProperties(tbCertConfig, newCertConfig);
        newCertConfig.setId(null);
        newCertConfig.setCreateTime(new Date());
        newCertConfig.setModifyTime(new Date());
        newCertConfig.setCertReal(0);
        tbCertConfigMapper.insertSelective(newCertConfig);
        log.info("copeCert. newCertConfig:{}", JsonUtils.objToString(newCertConfig));
        Integer newCertConfigId = newCertConfig.getId();
        List<TbFiledInfo> tbFiledInfoList = tbFiledInfoMapper.selectByCertConfigId(certConfigId);
        //复制字段表
        if (CollectionUtils.isNotEmpty(tbFiledInfoList)) {
            tbFiledInfoList.stream().forEach(tbFiledInfo -> {
                TbFiledInfo newFiledInfo = new TbFiledInfo();
                BeanUtils.copyProperties(tbFiledInfo, newFiledInfo);
                newFiledInfo.setId(null);
                newFiledInfo.setCertConfigId(newCertConfigId);
                tbFiledInfoMapper.insertSelective(newFiledInfo);
                log.info("copeCert. newFiledInfo:{}", JsonUtils.objToString(newFiledInfo));
            });
        }
        //复制查询页
        TbQueryPage tbQueryPage = tbQueryPageMapper.selectByCertConfigId(certConfigId);
        TbQueryPage newQueryPage = new TbQueryPage();
        BeanUtils.copyProperties(tbQueryPage, newQueryPage);
        newQueryPage.setCertConfigId(newCertConfigId);
        newQueryPage.setId(null);
        tbQueryPageMapper.insertSelective(newQueryPage);
        log.info("copeCert. newQueryPage:{}", JsonUtils.objToString(newQueryPage));
        //复制查询结果页
        TbQueryResultPage tbQueryResultPage = tbQueryResultPageMapper.selectByCertConfigId(certConfigId);
        List<FiledInfo> fields = JsonUtils.stringToObj(JsonUtils.objToString(tbQueryResultPage.getEnableFields()),
            new TypeReference<List<FiledInfo>>() {
            });
        List<FiledInfo> newEnableFields = new ArrayList<>();
        fields.stream().forEach(enableField -> {
            enableField.setCertConfigId(newCertConfigId);
            newEnableFields.add(enableField);
        });
        TbQueryResultPage newQueryResultPage = new TbQueryResultPage();
        BeanUtils.copyProperties(tbQueryResultPage, newQueryResultPage);
        newQueryResultPage.setEnableFields(JsonUtils.objToString(newEnableFields));
        newQueryResultPage.setCertConfigId(newCertConfigId);
        newQueryResultPage.setId(null);
        tbQueryResultPageMapper.insertSelective(newQueryResultPage);
        log.info("copeCert. newQueryResultPage:{}", JsonUtils.objToString(newQueryResultPage));
        return newCertConfigId;
    }

}
