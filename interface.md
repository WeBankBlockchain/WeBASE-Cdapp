# 接口说明
## 1. H5接口

### 1.1. 查询证书

#### 接口描述

查询证书信息。

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/queryFuzzyFiledInfo?certConfigId={certConfigId}&checkCode={checkCode}&filedInfo={filedInfo}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文** | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |
| 2        | 验证码 | checkCode     | String   | 32           | 是       |  |
| 3        | 查询字段（如姓名或身份证） | filedInfo     | String   | 32           | 是       |  |

**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/queryFuzzyFiledInfo?certConfigId=106&checkCode=qNrk&filedInfo=%E9%99%88%E6%B4%81%E8%94%9A" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result        | object      |              |          |           |
| 3.1      | 编号     | id          | int       |              | 是       |                   |
| 3.2      | 证书配置编号 | certConfigId   | int    |            | 是       |                   |
| 3.3      | 显示字段 | enableFields    | List<fields>    |     | 是       |                   |
| 3.4     | 横幅图片 | bottomLog   | longtext  |              | 是       |                   |
| 3.5     | 底部文案  | explainDoc     | String    |          | 是       |                   |
| 3.6     | 证书标题 | title    | String    |                 | 是       |                   |
| 3.7     | 证书预览图 | picPreview  | longtext |             | 是       |                   |
| 3.8     | 证书配置字段 | fields  | List |                  | 是       |                   |

**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": "dd",
  "result": [
      {
        bottomLog: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkA"
        certConfigId: 106
        enableFields: [,…]
        explainDoc: "xxx机构提供技术支持"
        fields: null
        id: 51
        picPreview: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkA"
        title: "2022深圳国际金融科技大赛证书"
      }
  ]
}
```

b.异常返回结果示例（信息详情请参看附录1）

```
{
  "code": 10001000,
  "message": "system exception",
  "data": null
}
```

### 1.2. 获取验证码

#### 接口描述

获取验证码。

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/pictureCheckCode
```

#### 调用方法

HTTP GET

#### 请求参数

无

**1）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/pictureCheckCode" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |                   |


**2）数据格式**

a.请求正常返回结果

```
{
   "code": 0,
   "msg": "success",
   "bizSeqNo": null,
   "result": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJsAAAA8CAIAAAD+Gl+NAAADfElEQVR42u3cPU7sMBAH8NyCkoYDUFBzEaqn19NyLSTuwHko6fKQnhQs22P/58t2smOlAZbdxL8dezzx7vb1vcdxpWPbo12rhWiIRgvRaCEaLUSjhWiIRgvRaCEaLUSjhWiIRgvRaCEabXHRu8eX7MAfP6sjHp7/dI+bjlET1M+P9+pxYVTqkvFjnGgbNX1YV85DF+HUi+rBuhfuOI9qUAXdtBqqmET5/hgtSmn9nIpyNtV3k4nosDliTq6Lo/6/cpMUSdOJmjF2uuVM0QzsuHhE9PX5Hjy8RQXD6d+3p/I42Xo0Q6Jy2naKlB1+qLjogYeHY5XTnHam6IFKierDlDv0gaLH0+LP3+W0Qh1RMyqRst+kHQSmSCNRs9+kQWkuqnedL3qgsmbTRpZbDrwsVDzd1XDif120rltFyqZM/dq0OssKEmBwGcN6zq6ZlesqopmceBnTyIwogCztAjlZyxuQ6kwx2kCyRW3nutmETa03qnKUty3qOTIjXI6KYBlnSYWv/Ruzpgz1gqLtxagJKjWDWpVy0v+tJsCs8sINiVKLHIFo2cti0ewfqTBtuF5QtIqUzm1K1Ma6Je1lJ9TyrVO+0ADUhUSrkYqLlpNoVRHn7C5Aqeyp8SYYkPRO2GfURi0jFURFSkVHJVZTEWzMplSApj+C605xNM8XLfsXvGPD5dyZ91C7paJuxpulYxSqbbVhzl7ALmoWqV1UvJarLPOmruKb4a51wUVFd+YWCLxwz73LZl4/OmidavfT9usiqDtns5If6u6zr4wK1hAdIdpFFdQujkG4y8naLDFzTz2IusO7f71RkSogQkvB4NE5Yb+ufv0uTlYb6xZ9kYEVf0gZ0vBMlhNlXV578El3AFEPG4OK1JNtz+SsosgQZP5CggClVi+3Imp1heAAPpKzO8ZYnU+IGo+xVmPMpUT13S3LnlyDknvOy302bf1INfwske31ctOC04i6RqpHVFllxRecRw07rnETZhFR8+ToHN/DYLXrYClOp2rDmb5ZQ5NMribqtzDd/Ob5uQu+RtIxfSJwnQu26mXbZm6LDMXVnSIyY80s4J2vnVhUVqChyuUsYNmbYExhcvMzGIPKWo+zKkq2wTSs6LFVn9F8spn1pQTUqytjUTyRe3P+ig7IBmeh6qNNn5d2x1tjUY80evz9SOUs6zfkdD/laHvV/wBGcMnToczPEgAAAABJRU5ErkJggg=="
 }

```

b.异常返回结果示例（信息详情请参看附录1）

```
{
  "code": 10001000,
  "message": "system exception",
  "data": null
}
```

## 2. 管理台接口

### 2.1. 登录

#### 接口描述

用户登录

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/login
```

#### 调用方法

HTTP POST

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 用户名 | name     | String   | 32           | 是       |  |
| 2        | 密码 | pwd     | String   | 32           | 是       |  |


**2）数据格式**

```
curl -X POST "http://127.0.0.1:8081/certapp-cert/login" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"id\": 0, \"name\": \"admin\", \"pwd\": \"Test123\"}"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |                   |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": {
    "id": 1,
    "name": "admin",
    "pwd": "$2a$10$YmQMG0qDgpP1RzGuW0F4Z.HlMXTX9ILWzYRbNTF2VEiqVK9EYoRXe"
  }
}

```

b.异常返回结果示例（信息详情请参看附录1）

```
{
  "code": 10001000,
  "message": "system exception",
  "data": null
}
```

### 2.2. 上链

#### 接口描述

证书上链

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/cochain?certConfigId={certConfigId}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/cochain?certConfigId=80" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |                   |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": null
}

```

### 2.3. 拷贝配置项
   
   #### 接口描述
   
   拷贝配置项
   
   #### 接口URL
   
   ```
   http://127.0.0.1:8081/certapp-cert/cert/copeCert?certConfigId={certConfigId}
   ```
   
   #### 调用方法
   
   HTTP GET
   
   #### 请求参数
   
   **1）参数表**
   
   | **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
   | -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
   | 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |
   
   
   
   **2）数据格式**
   
   ```
   curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/copeCert?certConfigId=106" -H "accept: */*"
   ```
   
   #### 响应参数
   
   **1）参数表**
   
   | **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
   | -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
   | 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
   | 2        | 提示信息 | message     | String    |              | 是       |                   |
   | 3        | 返回数据 | result       | Object    |              |          |                   |
   
   
   **2）数据格式**
   
   a.请求正常返回结果
   
   ```
   {
     "code": 0,
     "msg": "success",
     "bizSeqNo": null,
     "result": 100
   }
   
   ```

### 2.4. 删除证书配置

#### 接口描述

删除证书配置

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/deleteById?id={id}
```

#### 调用方法

HTTP DELETE

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X DELETE "http://127.0.0.1:8081/certapp-cert/cert/deleteById?id=109" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |                   |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": null
}

```

b.异常返回结果示例（信息详情请参看附录1）

```
{
  "code": 10001000,
  "message": "system exception",
  "data": null
}
```

### 2.5. 下载所有证书
   
#### 接口描述

下载所有证书

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/download?certConfigId={certConfigId}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/download?certConfigId=106" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |   证书图片（base64)     |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": [
       {
       "id": 4261,
       "certPic": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkAAAARgCAIAAAA+z8T9+cM5IsG0jyrm+"
    }
 ]
}

```

### 2.6. 下载/预览单个证书
   
#### 接口描述

下载/预览单个证书

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/downloadById?id={id}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 编号 | id     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/downloadById?id=4261" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |  证书图片（base64)  |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkAAAARgCAIAAAA+z8T9+cM5IsG0jyrm+"
}

```

### 2.7. 证书列表
   
#### 接口描述

证书列表

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/getCertList
```

#### 调用方法

HTTP GET

#### 请求参数

无

**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/getCertList" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |               |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": [
    {
      "certConfigId": xxx,
      "certName": "证书配置模板",
      "certState": 0,
      "isCertPic": 0,
      "certReal": true
    }
  ]
}

```

### 2.8. 获取导入字段数据
   
#### 接口描述

获取导入字段数据

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/getFiledInfo?certConfigId={certConfigId}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/getFiledInfo?certConfigId=106" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |     |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": {
    "certState": 2,
    "certFiledInfo": [
      {
        "filedName": "certificatID",
        "filedNameZh": "证书编号",
        "filedSerial": "0"
      },
      {
        "filedName": "name",
        "filedNameZh": "姓名",
        "filedSerial": "1"
      },
      {
        "filedName": "team",
        "filedNameZh": "参赛团队",
        "filedSerial": "2"
      },
      {
        "filedName": "track",
        "filedNameZh": "赛道",
        "filedSerial": "3"
      },
      {
        "filedName": "award",
        "filedNameZh": "奖项",
        "filedSerial": "4"
      },
      {
        "filedName": "number",
        "filedNameZh": "身份证",
        "filedSerial": "5"
      }
    ],
    "filedInfoResp": [
      {
        "id": 4261,
        "filedInfo": "2022010114;张三;Intelli5;人工智能;二等奖;44080219990318xxxx"
      }
    ]
  }
}

```

### 2.9. 获取查询页配置
   
#### 接口描述

获取查询页配置

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/getQueryPage?certConfigId={certConfigId}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/getQueryPage?certConfigId=106" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |       |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": {
    banner: "data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAA
    bottomLog ： "data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAA"
    certConfigId: xxx
    certOrg: "xxx机构"
    explainDoc: "xxx机构提供技术支持"
    fields: [{id: 1238, certConfigId: 107, filedSerial: "0", filedName: "certificatID", filedNameZh: "证书编号",…},…]
    filedSerial: "0"
    id: 49
    searchHint: "请输入证书编号查询"
  }
}

```

### 2.10. 获取查询结果页配置
   
#### 接口描述

获取查询结果页配置

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/getQueryResultPage?certConfigId={certConfigId}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/getQueryResultPage?certConfigId=106" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |     |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": {
    bottomLog ： "data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAA"
    certConfigId: xxx
    enableFields: [,…]
    explainDoc: "xxx机构提供技术支持"
    fields: [,…]
    id: 52
    picPreview: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkA
    title: "2022深圳国际金融科技大赛证书"
  }
}

```

### 2.11. 导入数据
   
#### 接口描述

导入数据

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert//certapp-cert/cert/importData
```

#### 调用方法

HTTP POST

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |
| 2        | excel文件 | file     | MultipartFile   |            | 是       |  |


**2）数据格式**

```
curl -X POST "http://127.0.0.1:8081/certapp-cert/cert/importData" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |  |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": null
}

```

### 2.12. 预览证书图片
   
#### 接口描述

预览证书图片

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/previewCertPic?id={id}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 编号 | id     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/previewCertPic?id=106" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |  证书图片（base64)  |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkAAAARgCAIAAAA+z8T9+cM5IsG0jyrm+"
}

```

### 2.13. 查询证书图片配置
   
#### 接口描述

查询证书图片配置

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/queryCertPic?id={id}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 编号 | id     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/queryCertPic?id=106" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |    |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": {
    baseMap: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkA
    certDesc: null
    certName: "证书配置模板"
    createTime: 1691955391000
    fields: [,…]
    filedFont: "28"
    id: 103
    modifyTime: 1691984191000
    numFont: "34"
    picPreview: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkA
    qrCodeLocal: {x: 1290, y: 244, w: 174, h: 174}
    qrCodeUrl: "ceshi.fisco.com.cn/#/pages/index/detail?certId=%s"
 }
}

```

### 2.14. 查询证书配置字段
   
#### 接口描述

查询证书配置字段

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/queryFiledByCertId?certConfigId={certConfigId}
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   | 32           | 是       |  |



**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/queryFiledByCertId?certConfigId=106" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |    |


**2）数据格式**

a.请求正常返回结果

```
{
  "code": 0,
  "msg": "success",
  "bizSeqNo": null,
  "result": [
    {
      "certConfigId": 106,
      "filedSerial": "0",
      "filedName": "certificatID",
      "filedNameZh": "证书编号",
      "showEnable": 1,
      "filedDemo": "2023080401",
      "x": 284,
      "w": 145,
      "y": 970,
      "h": 39
    },
    {
      "certConfigId": 106,
      "filedSerial": "1",
      "filedName": "name",
      "filedNameZh": "姓名",
      "showEnable": 1,
      "filedDemo": "张三",
      "x": 234,
      "w": 180,
      "y": 518,
      "h": 48
    },
    {
      "certConfigId": 106,
      "filedSerial": "2",
      "filedName": "team",
      "filedNameZh": "参赛团队",
      "showEnable": 1,
      "filedDemo": "区块链团队",
      "x": 647,
      "w": 380,
      "y": 518,
      "h": 48
    },
    {
      "certConfigId": 106,
      "filedSerial": "3",
      "filedName": "track",
      "filedNameZh": "赛道",
      "showEnable": 1,
      "filedDemo": "区块链",
      "x": 1108,
      "w": 180,
      "y": 588,
      "h": 48
    },
    {
      "certConfigId": 106,
      "filedSerial": "4",
      "filedName": "award",
      "filedNameZh": "奖项",
      "showEnable": 1,
      "filedDemo": "一等奖",
      "x": 377,
      "w": 146,
      "y": 658,
      "h": 48
    },
    {
      "certConfigId": 106,
      "filedSerial": "5",
      "filedName": "number",
      "filedNameZh": "身份证",
      "showEnable": 0,
      "filedDemo": "442524199745251254",
      "x": 0,
      "w": 0,
      "y": 0,
      "h": 0
    }
  ]
}

```

### 2.15. 添加/修改证书图片配置
   
#### 接口描述

添加/修改证书图片配置

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/saveCertPic
```

#### 调用方法

HTTP POST

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 底图 | baseMap     | longtext   |            | 是       |  |
| 2        | 证书描述 | certDesc     | String   |      64      | 是       |  |
| 3        | 证书名称 | certName     | String   |      64      | 是       |  |
| 4        | 证书配置字段 | fields     | List   |            | 是       |  |
| 5        | 字段字体大小 | filedFont     | String   |      64      | 是       |  |
| 6        | 证书编号字体大小 | numFont     | String   |     64      | 是       |  |
| 7        | 证书预览图 | picPreview     | longtext   |            | 是       |  |
| 8        | 二维码位置 | qrCodeLocal     | String   |      64      | 是       |  |
| 9        | 证书配置编号 | certConfigId     | int   |      32      | 是       | 为空新增，不为空修改  |




**2）数据格式**

```
curl -X POST "http://127.0.0.1:8081/certapp-cert/cert/saveCertPic" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |    |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": {
    baseMap: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkA
    certDesc: null
    certName: "证书配置模板"
    createTime: 1692912099000
    fields: [,…]
    filedFont: "28"
    id: 103
    modifyTime: 1692940899000
    numFont: "34"
    picPreview: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABkA
    qrCodeLocal: {x: 1290, y: 244, w: 174, h: 174}
    qrCodeUrl: "ceshi.fisco.com.cn/#/pages/index/detail?certId=%s"
 }
}

```

### 2.16. 添加/修改查询页配置
   
#### 接口描述

添加/修改查询页配置

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/saveQueryPage
```

#### 调用方法

HTTP POST

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 横幅图片 | banner    | longtext   |            | 是       |  |
| 2        | 底部log | bottomLog     | longtext   |           | 是       |  |
| 3        | 发证机构 | certOrg     | String   |      32      | 是       |  |
| 4        | 技术支持说明文案 | explainDoc     | String   |    32        | 是       |  |
| 5        | 搜索提示语 | searchHint     | String   |      32      | 是       |  |
| 6        | 字段序号 | filedSerial     | String   |     32      | 是       |  |
| 7        | 证书配置编号 | certConfigId     | int   |      32      | 是       | 为空新增，不为空修改  |




**2）数据格式**

```
curl -X POST "http://127.0.0.1:8081/certapp-cert/cert/saveQueryPage" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |    |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": {
    banner: "data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAA"
    bottomLog: "data:image/jpeg;base64,/9j/4QAYRXhpZgAASUkqAAgAAA"
    certConfigId: 103
    certOrg: "xxx机构"
    explainDoc: "xxx机构提供技术支持"
    fields: null
    filedSerial: "1"
    id: 43
    searchHint: "请输入姓名查询"
 }
}

```

### 2.17. 添加/修改查询结果页配置
   
#### 接口描述

添加/修改查询结果页配置

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/saveQueryResultPage
```

#### 调用方法

HTTP POST

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   |      32      | 是       | 为空新增，不为空修改  |
| 2        | 底部log | bottomLog     | longtext   |           | 是       |  |
| 3        | 标题 | title     | String   |      32      | 是       |  |
| 4        | 技术支持说明文案 | explainDoc     | String   |    32        | 是       |  |
| 5        | 显示字段 | enableFields     | List   |            | 是       |  |


**2）数据格式**

```
curl -X POST "http://127.0.0.1:8081/certapp-cert/cert/saveQueryResultPage" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |    |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": {
    bottomLog: "data:image/png;base64,iVBORw0KGgoAAAANSUh"
    certConfigId: 103
    enableFields: "[{\"certConfigId\":103,\"filedSerial\":\"4\",\"filedName\":\"award\",\"filedNameZh\":\"奖项\",\"filedLocal\":null,\"showEnable\":1,\"filedDemo\":\"一等奖\"}, {\"certConfigId\":103,\"filedSerial\":\"3\",\"filedName\":\"track\",\"filedNameZh\":\"赛道\",\"filedLocal\":null,\"showEnable\":1,\"filedDemo\":\"区块链\"}, {\"certConfigId\":103,\"filedSerial\":\"1\",\"filedName\":\"name\",\"filedNameZh\":\"姓名\",\"filedLocal\":null,\"showEnable\":1,\"filedDemo\":\"张三\"}, {\"certConfigId\":103,\"filedSerial\":\"2\",\"filedName\":\"team\",\"filedNameZh\":\"参赛团队\",\"filedLocal\":null,\"showEnable\":1,\"filedDemo\":\"区块链团队\"}]"
    explainDoc: "xxx机构提供技术支持"
    fields: null
    title: "2022深圳国际金融科技大赛证书"
 }
}

```

### 2.18. 修改证书发布状态
   
#### 接口描述

修改证书发布状态

#### 接口URL

```
http://127.0.0.1:8081/certapp-cert/cert/updateCertState?certConfigId={certConfigId}&state=true
```

#### 调用方法

HTTP GET

#### 请求参数

**1）参数表**

| **序号** | **中文**     | **参数名** | **类型** | **最大长度** | **必填** | **说明**     |
| -------- | ------------ | ---------- | -------- | ------------ | -------- | ------------ |
| 1        | 证书配置编号 | certConfigId     | int   |      32      | 是       | 为空新增，不为空修改  |
| 2        | 底部log | bottomLog     | longtext   |           | 是       |  |
| 3        | 标题 | title     | String   |      32      | 是       |  |
| 4        | 技术支持说明文案 | explainDoc     | String   |    32        | 是       |  |
| 5        | 显示字段 | enableFields     | List   |            | 是       |  |


**2）数据格式**

```
curl -X GET "http://127.0.0.1:8081/certapp-cert/cert/updateCertState?certConfigId=106&state=true" -H "accept: */*"
```

#### 响应参数

**1）参数表**

| **序号** | **中文** | **参数名**  | **类型**  | **最大长度** | **必填** | **说明**          |
| -------- | -------- | ----------- | --------- | ------------ | -------- | ----------------- |
| 1        | 返回码   | code        | String    |              | 是       | 返回码信息请附录1 |
| 2        | 提示信息 | message     | String    |              | 是       |                   |
| 3        | 返回数据 | result       | Object    |              |          |    |


**2）数据格式**

a.请求正常返回结果

```
{
 "code": 0,
 "msg": "success",
 "bizSeqNo": null,
 "result": null
}

```

## 附录 

### 1. 返回码信息列表

| Code     | message          | 描述       |
| -------- | ---------------- | ---------- |
| 0        | success          | 正常       |
| 10001000 | system exception | 系统异常   |
| 10001001 | 10001001         | 数据库异常 |