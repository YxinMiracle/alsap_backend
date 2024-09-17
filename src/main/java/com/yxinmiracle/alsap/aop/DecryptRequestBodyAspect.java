package com.yxinmiracle.alsap.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.utils.CryptoUtils;
import com.yxinmiracle.alsap.utils.HttpHelper;
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
import java.util.Enumeration;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class DecryptRequestBodyAspect {

    @Value("${decrypt.secretKey}")
    private String secretKey;

    @Value("${headerKey.nonceKey}")
    private String nonceKey;

    @Value("${headerKey.requestTimestampKey}")
    private String requestTimestampKey;

    @Value("${headerKey.signatureKey}")
    private String signatureKey;

    @Value("${fixedValue}")
    private String fixedValue;


    @Around("@annotation(com.yxinmiracle.alsap.annotation.DecryptRequestBody)")
    public Object decryptBody(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String params = HttpHelper.getBodyString(request);
        String jsonSt = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonSt);
        String encryptedData = rootNode.path(secretKey).asText();
        if (encryptedData == null) {
            throw new BusinessException(ErrorCode.ILLEGALITY_REQUEST_ERROR);
        }

        // 获取请求头
        Optional<String> nonceOptional = Optional.ofNullable(request.getHeader(nonceKey));
        Optional<String> requestTimestampOptional = Optional.ofNullable(request.getHeader(requestTimestampKey));
        Optional<String> signatureOptional = Optional.ofNullable(request.getHeader(signatureKey));

        // 检查是否存在
        if (!nonceOptional.isPresent() || !requestTimestampOptional.isPresent() || !signatureOptional.isPresent()) {
            throw new BusinessException(ErrorCode.ILLEGALITY_REQUEST_ERROR);
        }

        // 随机值
        String nonce = nonceOptional.get();
        // 时间搓
        String requestTimestamp = requestTimestampOptional.get();
        // 加密值
        String signature = signatureOptional.get();

        if (!CryptoUtils.isTimestampValid(Long.parseLong(requestTimestamp))) {
            throw new BusinessException(ErrorCode.TIME_REQUEST_ERROR);
        }


        try {
            String decryptedData = CryptoUtils.decryptAndDecodeBase64(encryptedData);
            System.out.println(decryptedData);
            if (!CryptoUtils.validateRequest(signature, requestTimestamp, nonce, fixedValue))
                throw new BusinessException(ErrorCode.ILLEGALITY_REQUEST_ERROR);
            Object[] args = buildArguments(joinPoint, decryptedData, request);
            return joinPoint.proceed(args);
        } catch (Exception e) {
            log.error("用户出现不合法请求,{}", e.getMessage());
            throw new BusinessException(ErrorCode.ILLEGALITY_REQUEST_ERROR);
        }
    }


    private Object[] buildArguments(ProceedingJoinPoint joinPoint, String decryptedData, HttpServletRequest request) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        ObjectMapper mapper = new ObjectMapper();

        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (HttpServletRequest.class.isAssignableFrom(parameterTypes[i])) {
                args[i] = request;
            } else {
                args[i] = mapper.readValue(decryptedData, parameterTypes[i]);
            }
        }
        return args;
    }
}
