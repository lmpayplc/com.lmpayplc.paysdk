package com.awesome.lemapaysdkdemo.common.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SignUtil {

    /**
     * 生成签名
     *
     * @param map
     * @return
     */
    public static String getSign(Map<String, String> map, String appkey) {

        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, (o1, o2) -> (o1.getKey()).compareTo(o2.getKey()));

            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!val.equals("")) {
                        sb.append(key + "=" + val + "&");
                    }
                }

            }
            Log.i("toString", sb.toString());
            sb.append("key=").append(appkey);
            Log.i("toString before:", result);
            //进行MD5加密
            result = MD5Util.INSTANCE.md5(sb.toString());
        } catch (Exception e) {
            return null;
        }
        return result;
    }
}
