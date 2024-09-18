package com.yxinmiracle.alsap.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.utils.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class DecryptInnerRequestBodyAspect {

    @Value("${innerRequest.requestTimestampKey}")
    private String requestTimestampKey;

    @Value("${innerRequest.signatureKey}")
    private String signatureKey;

    /**
     * 对内部请求进行加密解密
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.yxinmiracle.alsap.annotation.DecryptInnerRequestBody)")
    public Object decryptBody(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 获取请求头
        Optional<String> requestTimestampOptional = Optional.ofNullable(request.getHeader(requestTimestampKey));
        Optional<String> signatureOptional = Optional.ofNullable(request.getHeader(signatureKey));

        // 检查是否存在
        if (!requestTimestampOptional.isPresent() || !signatureOptional.isPresent()) {
            throw new BusinessException(ErrorCode.ILLEGALITY_REQUEST_ERROR);
        }

        // 时间搓
        String requestTimestamp = requestTimestampOptional.get();
        // 加密值
        String signature = signatureOptional.get();

        if (!CryptoUtils.isTimestampValid(Long.parseLong(requestTimestamp))) {
            throw new BusinessException(ErrorCode.TIME_REQUEST_ERROR);
        }

        try {
            if (!CryptoUtils.validateInnerRequest(signature))
                throw new BusinessException(ErrorCode.ILLEGALITY_REQUEST_ERROR);
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("用户出现不合法请求,{}", e.getMessage());
            throw new BusinessException(ErrorCode.ILLEGALITY_REQUEST_ERROR);
        }
    }

}
