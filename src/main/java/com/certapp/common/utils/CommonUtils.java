/*
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
package com.certapp.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.v3.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.v3.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.utils.Numeric;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * CommonUtils. todo use java sdk
 */
@Slf4j
public class CommonUtils {

    public static final int PUBLIC_KEY_LENGTH_64 = 64;
    public static final int HASH_LENGTH_64 = 64;
    private static final char[] CHARS = {'2', '3', '4', '5', '6', '7', '8', '9', 'a',
        'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's',
        't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
        'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * stringToSignatureData. 19/12/24 support guomi： add byte[] pub in signatureData byte array: [v
     * + r + s + pub] 2021/08/05 webase-sign <=1.4.3, v=27 >=1.5.0, v=0 if using web3sdk,
     * Signature's v default 27, if using java-sdk, SignatureResult's v default 0, and add 27 in RLP
     * encode
     * 
     * @param signatureData signatureData
     * @return
     */
    public static SignatureResult stringToSignatureData(String signatureData, int encryptType) {
        byte[] byteArr = Numeric.hexStringToByteArray(signatureData);
        // 从1开始，因为此处webase-sign返回的byteArr第0位是v
        byte signV = byteArr[0];
        byte[] signR = new byte[32];
        System.arraycopy(byteArr, 1, signR, 0, signR.length);
        byte[] signS = new byte[32];
        System.arraycopy(byteArr, 1 + signR.length, signS, 0, signS.length);
        if (encryptType == CryptoType.SM_TYPE) {
            byte[] pub = new byte[64];
            System.arraycopy(byteArr, 1 + signR.length + signS.length, pub, 0, pub.length);
            return new SM2SignatureResult(pub, signR, signS);
        } else {
            return new ECDSASignatureResult(signV, signR, signS);
        }
    }


    /**
     * signatureDataToString. 19/12/24 support guomi： add byte[] pub in signatureData
     * 
     * @param signatureData signatureData
     */

    public static String signatureDataToString(SM2SignatureResult signatureData) {
        byte[] byteArr;
        byteArr = new byte[1 + signatureData.getR().length + signatureData.getS().length
                + PUBLIC_KEY_LENGTH_64];
        // v
        byteArr[0] = 0;
        // r s
        System.arraycopy(signatureData.getR(), 0, byteArr, 1, signatureData.getR().length);
        System.arraycopy(signatureData.getS(), 0, byteArr, signatureData.getR().length + 1,
                signatureData.getS().length);
        System.arraycopy(signatureData.getPub(), 0, byteArr,
                signatureData.getS().length + signatureData.getR().length + 1,
                signatureData.getPub().length);

        return Numeric.toHexString(byteArr, 0, byteArr.length, false);
    }

    public static String signatureDataToString(ECDSASignatureResult signatureData) {
        byte[] byteArr;
        byteArr = new byte[1 + signatureData.getR().length + signatureData.getS().length];
        byteArr[0] = signatureData.getV();
        System.arraycopy(signatureData.getR(), 0, byteArr, 1, signatureData.getR().length);
        System.arraycopy(signatureData.getS(), 0, byteArr, signatureData.getR().length + 1,
                signatureData.getS().length);
        return Numeric.toHexString(byteArr, 0, byteArr.length, false);
    }

    /**
     * parse Byte to HexStr.
     * 
     * @param buf byte
     * @return
     */
    public static String parseByte2HexStr(byte[] buf) {
        log.info("parseByte2HexStr start...");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        log.info("parseByte2HexStr end...");
        return sb.toString();
    }

    /**
     * parse String to HexStr.
     * 
     * @param str String
     * @return
     */
    public static String parseStr2HexStr(String str) {
        if (StringUtils.isBlank(str)) {
            return "0x0";
        }
        return "0x" + Integer.toHexString(Integer.valueOf(str));
    }

    /**
     * base64Decode.
     *
     * @param str String
     * @return
     */
    public static byte[] base64Decode(String str) {
        if (str == null) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(str);
    }

    /**
     * get server ip.
     * 
     * @return
     */
    public static String getCurrentIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces =
                    NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = nias.nextElement();
                    if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress()
                            && ia instanceof Inet4Address) {
                        return ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            log.error("getCurrentIp error.");
        }
        return null;
    }

    /**
     * parseHexStr2Int.
     *
     * @param str str
     * @return
     */
    public static int parseHexStr2Int(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        return Integer.parseInt(str.substring(2), 16);
    }

    /**
     * 支持数字
     * 
     * @param input
     * @return
     */
    public static boolean isDigit(String str) {
        if (str != null && !"".equals(str.trim())) {
            String regex = "^[0-9]+$";
            return str.matches(regex);
        }
        return false;
    }

    /**
     * 支持数字，字母
     * 
     * @param input
     * @return
     */
    public static boolean isLetterDigit(String input) {
        String regex = "^[a-z0-9A-Z]+$";
        return input.matches(regex);
    }

    /**
     * 不包含中文
     */
    public static boolean notContainsChinese(String input) {
        if (StringUtils.isBlank(input)) {
            return true;
        }
        String regex = "[^\\u4e00-\\u9fa5]+";
        return input.matches(regex);
    }

    /**
     * check connect.
     */
    public static boolean checkConnect(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setReceiveBufferSize(8193);
            socket.setSoTimeout(500);
            SocketAddress address = new InetSocketAddress(host, port);
            socket.connect(address, 1000);
        } catch (Exception ex) {
            log.info("fail checkConnect.");
            return false;
        } finally {
            if (Objects.nonNull(socket)) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("fail close socket", e);
                }
            }
        }
        return true;
    }

    /**
     * extractFigureFromStr.
     * 
     * @param str
     * @return
     */
    public static int extractFigureFromStr(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return Integer.parseInt(m.replaceAll("").trim());
    }

    /**
     * getFolderSize.
     * 
     * @param
     * @return
     */
    public static long getFolderSize(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFolderSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 文件压缩并Base64加密
     * 
     * @param srcFiles
     * @return
     */
    public static String fileToZipBase64(List<File> srcFiles) {
        long start = System.currentTimeMillis();
        String toZipBase64 = "";
        ZipOutputStream zos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            zos = new ZipOutputStream(baos);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[1024];
                log.info("fileToZipBase64 fileName: [{}] size: [{}] ", srcFile.getName(),
                        srcFile.length());
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                try (FileInputStream in = new FileInputStream(srcFile)) {
                    while ((len = in.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                }
                zos.closeEntry();
            }
            long end = System.currentTimeMillis();
            log.info("fileToZipBase64 cost time：[{}] ms", (end - start));
        } catch (IOException e) {
            log.error("fileToZipBase64 IOException:[{}]", e.toString());
        } finally {
            close(zos);
        }

        byte[] refereeFileBase64Bytes = Base64.getEncoder().encode(baos.toByteArray());
        try {
            toZipBase64 = new String(refereeFileBase64Bytes, "UTF-8");
        } catch (IOException e) {
            log.error("fileToZipBase64 IOException:[{}]", e.toString());
        }
        return toZipBase64;
    }

    private static String cleanString(String str) {
        if (str == null) {
            return null;
        }
        String cleanString = "";
        for (int i = 0; i < str.length(); ++i) {
            cleanString += cleanChar(str.charAt(i));
        }
        return cleanString;
    }

    private static char cleanChar(char value) {
        // 0 - 9
        for (int i = 48; i < 58; ++i) {
            if (value == i) {
                return (char) i;
            }
        }
        // 'A' - 'Z'
        for (int i = 65; i < 91; ++i) {
            if (value == i) {
                return (char) i;
            }
        }
        // 'a' - 'z'
        for (int i = 97; i < 123; ++i) {
            if (value == i) {
                return (char) i;
            }
        }
        // other valid characters
        switch (value) {
            case '\\':
                return '\\';
            case '/':
                return '/';
            case ':':
                return ':';
            case '.':
                return '.';
            case '-':
                return '-';
            case '_':
                return '_';
            default:
                return ' ';
        }
    }

    /**
     * close Closeable.
     * 
     * @param closeable object
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("closeable IOException:[{}]", e.toString());
            }
        }
    }

    public static Bytes32 utf8StringToBytes32(String utf8String) {
        String hexStr = utf8StringToHex(utf8String);
        return hexStrToBytes32(hexStr);
    }

    public static Bytes32 hexStrToBytes32(String hexStr) {
        byte[] byteValue = Numeric.hexStringToByteArray(hexStr);
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    public static String utf8StringToHex(String utf8String) {
        return Numeric.toHexStringNoPrefix(utf8String.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean isHexNumber(String hexString) {
        if (hexString == null)
            throw new NullPointerException("hexString was null");

        String pattern = "^[A-Fa-f0-9]+$";
        return Pattern.compile(pattern).matcher(hexString).matches();
    }

    /**
     * convert hex number string to decimal number string
     * 
     * @param receipt
     */
    public static void processReceiptHexNumber(TransactionReceipt receipt) {
        log.info("processReceiptHexNumber. receipt:[{}]", JacksonUtils.toJSONString(receipt));
        if (receipt == null) {
            return;
        }
        String gasUsed = Optional.ofNullable(receipt.getGasUsed()).orElse("0");
        String blockNumber = Optional.ofNullable(receipt.getBlockNumber()).orElse("0");
        receipt.setGasUsed(Numeric.toBigInt(gasUsed).toString(10));
        receipt.setBlockNumber(Numeric.toBigInt(blockNumber).toString(10));
    }


    /**
     * get version number without character
     * 
     * @param verStr ex: v2.4.1, ex 1.5.0
     * @return ex: 241, 150
     */
    public static int getVersionFromStr(String verStr) {
        log.info("getVersionFromStr verStr:{}", verStr);
        // remove v and split
        if (verStr.toLowerCase().startsWith("v")) {
            verStr = verStr.substring(1);
        }
        String[] versionArr = verStr.split("\\.");
        if (versionArr.length < 3) {
            log.error("getVersionFromStr versionArr:{}", (Object) versionArr);
            return 0;
        }
        // get num
        int version = Integer.parseInt(versionArr[0]) * 100 + Integer.parseInt(versionArr[1]) * 10
                + Integer.parseInt(versionArr[2]);
        log.info("getVersionFromStr version:{}", version);
        return version;
    }


    /**
     * delete dir or file whatever
     * 
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children == null) {
                return dir.delete();
            }
            // recursive delete until dir is emtpy to delete
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // delete empty dir
        return dir.delete();
    }

    /**
     * 字母开头
     * 
     * @param input
     * @return
     */
    public static boolean startWithLetter(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        if (!isLetterDigit(input)) {
            return false;
        }
        String regex = "^[a-zA-Z]+$";
        return (input.charAt(0) + "").matches(regex);
    }

    public static String md5Encrypt(String dataStr) {
        try {
            if (StringUtils.isBlank(dataStr)) {
                return "";
            }
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result.toLowerCase();
        } catch (Exception e) {
            log.error("fail md5Encrypt", e);
        }
        return "";
    }

    /**
     * 获取指定位数的数字和字母组合的字符串
     *
     * @param length 字符串长度
     */
    public static String randomString(int length) throws NoSuchAlgorithmException {
        if (length > CHARS.length) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }
}
