package com.yxinmiracle.alsap.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Map;


@Slf4j
public class RequestUtils {

    /**
     * 超时毫秒数。http的连接与读取
     */
    private static final int TIME_OUT_MILLISECONDS = 60000;

    /**
     * Content-Type
     */
    private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";

    /**
     * GET请求
     */
    public static HttpResponse get(String url, Map<String, String> headerMap) {
        Long currentTime = System.currentTimeMillis();
        log.info("[RequestUtils.get  request...] in: currentTime={}, url={}, headerMap={}", currentTime, url, headerMap);
        HttpResponse response = HttpRequest
                .get(url)
                .timeout(TIME_OUT_MILLISECONDS)
                .addHeaders(headerMap)
                .execute();
        return response;
    }


    public static <T> T post(String url, String postBody, Map<String, String> headerMap, Class<T> responseClassType) {
        Long currentTime = System.currentTimeMillis();
        log.info("[RequestUtils.post  request...] in: currentTime={}, url={}, postBody={}, headerMap={}", currentTime, url, postBody, headerMap);
        // 获取到对应的相应str
        String responseStr = HttpRequest
                .post(url)
                .body(postBody, CONTENT_TYPE_JSON)
                .timeout(TIME_OUT_MILLISECONDS)
                .addHeaders(headerMap)
                .execute()
                .body();
        T responseBean = JSONUtil.toBean(responseStr, responseClassType);
        ThrowUtils.throwIf(com.yxinmiracle.alsap.utils.ObjectUtils.areAllFieldsNull(responseBean), ErrorCode.AI_SERVER_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(responseBean), ErrorCode.AI_SERVER_ERROR);
        return responseBean;
    }

}