package com.apisign.demo.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * param参数自然排序
 * TODO 添加上请求头信息==>appId+timestamp+nonce 参与sign
 * 1.参数自然排序
 * 2.最后追加上key=${appSecret},动态值, eq. appid=appId&noncestr=6&timestamp=1609944592558& + key=${appSecret}
 * 3.md5
 * 4.添加到请求头上sign
 */
public class SignUtil {
    private static Logger log = LoggerFactory.getLogger(SignUtil.class);

    public static String getSign(Map<String, Object> headMap, Map<String, Object> map, String appSecret) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Object> headEntry : headMap.entrySet()) {
            if (headEntry.getValue() != "" && headEntry.getValue() != null) {
                list.add(headEntry.getKey() + "=" + headEntry.getValue() + "&");
            }
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != "" && entry.getValue() != null) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();

        result += "key=" + appSecret;
        log.info("签名前：" + result);
        result = md5(result);
        log.info("签名后：" + result);
        return result;
    }

    private static String md5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, md5.digest())).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("Signature.MD5 NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            log.error("Signature.MD5 UnsupportedEncodingException", e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> headMap = new HashMap<>(6);
        headMap.put("appId", "pos");
        headMap.put("timestamp", System.currentTimeMillis());
        headMap.put("nonce", new Random().nextInt(10) + "");

        Map<String, Object> map = new HashMap<>(6);
        map.put("userid", "123");
        map.put("username", "admin");


        getSign(headMap, map, "appSecret");
    }
}
