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

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public class FileUploadUtil {
    /**
     * 拿到文件并上传 到指定位置
     * @param file ：获取到的文件
     * @param filePath ：路径
     * @param fileName ：文件名称
     * @return
     * @throws Exception
     */
    public static String fileUpload(MultipartFile file, String filePath, String fileName)
        throws Exception {
        //扩展名
        String exeName = "";
        if (file.getOriginalFilename().lastIndexOf(".") >= 0) {
            exeName = file.getOriginalFilename().
                substring(file.getOriginalFilename().lastIndexOf("."));
        }
        copyFile(file.getInputStream(), filePath, fileName + exeName);
        return fileName + exeName;
    }


    private static File copyFile(InputStream inputStream, String dir, String realName) throws Exception {

        File destFile = new File(dir, realName);
        return copyFile(inputStream, destFile);
    }


    private static File copyFile(InputStream inputStream, File destFile) throws Exception {
        if (null == inputStream) {
            return null;
        }
        if (!destFile.exists()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdir();
            }
            destFile.createNewFile();
        }
        FileUtils.copyInputStreamToFile(inputStream, destFile);
        return destFile;
    }
}
