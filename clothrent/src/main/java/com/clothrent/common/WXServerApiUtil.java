package com.clothrent.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created  at 2024/10/1.
 */
public class WXServerApiUtil {
    private static Logger log = LoggerFactory.getLogger(WXServerApiUtil.class.getName());
    private static String grant_type="authorization_code";

    public static JSONObject code2Session(String appid, String secret, String js_code){

        String url = "https://api.weixin.qq.com/sns/jscode2session?";

        if (StringUtils.isEmpty(appid)||StringUtils.isEmpty(secret)||StringUtils.isEmpty(js_code)){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("appid=").append(appid).append("&secret=").append(secret).append("&js_code=").append(js_code);
        stringBuffer.append("&grant_type=").append(grant_type);
        url=url+stringBuffer.toString();
        log.debug("拼接好的微信请求url："+url);
        try {
            String result = HttpUtils.sendGet(url);
            log.debug("查询得到的微信返回结果："+result);
            return JSON.parseObject(result);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }
}
