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
package com.certapp.entity.response;

import com.certapp.entity.*;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class CertConfigResp {
    private Integer id;
    private String certName;
    private String baseMap;
    private List<FiledInfoVo> fields;
    private String filedFont;
    private String qrCodeUrl;
    private QrCodeLocal qrCodeLocal;
    private String numFont;
    private String picPreview;
    private String certDesc;
    private Date createTime;
    private Date modifyTime;
}
