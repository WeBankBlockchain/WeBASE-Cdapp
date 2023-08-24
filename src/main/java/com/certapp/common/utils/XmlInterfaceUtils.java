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
package com.certapp.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class XmlInterfaceUtils {
    /**
     * 对字节数组字符串进行Base64解码并生成文件
     * @param fileStr 文件base64位数据
     * @param fileFilePath 保存文件全路径地址
     * @return
     */
    public static boolean generateBase64StringToFile(String fileStr,String fileFilePath){
        if (fileStr == null) //文件base64位数据为空
            return false;
        try
        {
            //Base64解码
            byte[] b = Base64.decodeBase64(fileStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成文件
            OutputStream out = new FileOutputStream(fileFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static String fileToBase64(String path) {
        String base64 = null;
        try {
            log.info("fileToBase64 path:{}", path);
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            base64 = java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("fileToBase64 err" + e);
        }
        return base64;
    }
}
