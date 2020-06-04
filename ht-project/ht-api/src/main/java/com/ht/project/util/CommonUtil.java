package com.ht.project.util;

import java.util.regex.Pattern;

public class CommonUtil {

    public static long getLongTimeStamp(){
        return (System.currentTimeMillis());
    }


    /**
     * 判断符串是否为空
     *
     * @param obj
     * @return
     */
    public synchronized static boolean isNotNull(Object obj) {
        if ((obj != null) && (!obj.toString().equals(""))) {
            return true;
        }
        return false;
    }


    public static boolean isBase64Encode(String content){
        if(!isNotNull(content)){
            return false;
        }
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return  content.matches(base64Pattern);
    }
}
