package com.yxinmiracle.alsap.aiService;

/*
 * @author  YxinMiracle
 * @date  2023-09-20 10:11
 * @Gitee: https://gitee.com/yxinmiracle
 */


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.common.AiServerRet;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.CtiTtps;
import com.yxinmiracle.alsap.model.enums.TtpStatusEnum;
import com.yxinmiracle.alsap.model.model.ModelResult;
import com.yxinmiracle.alsap.model.vo.cti.CtiTtpExtractVo;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.service.CtiTtpsService;
import com.yxinmiracle.alsap.service.ItemService;
import com.yxinmiracle.alsap.service.RelationTypeService;
import com.yxinmiracle.alsap.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiServer {

    @Resource
    private CtiService ctiService;

    @Resource
    private RelationTypeService relationTypeService;

    @Resource
    private ItemService itemService;

    @Resource
    private CtiTtpsService ctiTtpsService;

    @Value("${aiServer.authHeader}")
    private String AUTH_HEADER;

    @Value("${aiServer.authSecret}")
    private String AUTH_REQUEST_SECRET;

    @Value("${aiServer.host}")
    public String AI_SERVER_URL;

    public static String ttpServerPath = "/annotation/cti";
    public static String extractCtiTtpPath = "/ttp/extract";
    public static String getGraphServePath = "/graph/cti";
    public static String copyModelData = "/copy";


    /**
     * 输入的信息是Cti情报的内容
     *
     * @param ctiContent
     * @return 通过调用Ai服务获取抽取出的cti情报实体的结果
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
    public ModelResult getCtiExtractorEntityAndRelationAns(String ctiContent) {
        // 构建请求数据
        Cti cti = new Cti();
        cti.setContent(ctiContent);
        String requestBody = JSONUtil.toJsonStr(cti);

        // 构建header
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTH_HEADER, AUTH_REQUEST_SECRET);

        // 开始进行请求
        return RequestUtils.post(AI_SERVER_URL + ttpServerPath, requestBody, headers, ModelResult.class);
    }

    /**
     * 根据cti的内容和id去获取关于这个cti的ttp内容
     * 调用ai服务的ttp抽取模型
     *
     * @param cti
     * @return
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
    public AiServerRet getCtiContentTtp(CtiTtpExtractVo cti) {
        // 构建请求数据
        String requestBody = JSONUtil.toJsonStr(cti);

        // 构建header，为了内部接口进行校验
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTH_HEADER, AUTH_REQUEST_SECRET);

        // 开始进行请求
        return RequestUtils.post(AI_SERVER_URL + extractCtiTtpPath, requestBody, headers, AiServerRet.class);
    }

    /**
     * 用来处理getCtiContentTtp方法请求失败后的逻辑
     * 这里是的逻辑是：统一请求状态，要是说ai服务请求回来的错误的话，状态码也不是0，所以这里只要把状态码设计为不为0就好
     * @param e
     * @param cti
     * @return
     */
    @Recover
    public AiServerRet getCtiContentTtpFailure(RuntimeException e, CtiTtpExtractVo cti) {
        log.error("getCtiContentTtp方法请求三次之后结果为失败，失败原因为: {}", e.getMessage());
        AiServerRet aiServerRet = new AiServerRet();
        aiServerRet.setCode(ErrorCode.AI_SERVER_ERROR.getCode());
        return aiServerRet;
    }

    public String pythonCreateGraph(Cti cti) {
        // 这里添加完之后，需要调用ai服务，进行模型的抽取
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ModelResult graphVo = new ModelResult();
        graphVo.setId(cti.getId());

        Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        Gson gson = new Gson();
        graphVo.setWordList(gson.fromJson(cti.getWordList(), type));
        graphVo.setLabelList(gson.fromJson(cti.getLabelList(), type));
        HttpEntity<ModelResult> entity = new HttpEntity<>(graphVo, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AI_SERVER_URL + getGraphServePath, entity, String.class);
        String res = responseEntity.getBody();
        return responseEntity.getBody();
    }

    public String copyModelData(Cti cti) {
        // 这里添加完之后，需要调用ai服务，进行模型的抽取
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ModelResult graphVo = new ModelResult();
        graphVo.setId(cti.getId());
        Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        Gson gson = new Gson();
        List<List<String>> wordList = gson.fromJson(cti.getWordList(), type);
        List<List<String>> labelList = gson.fromJson(cti.getLabelList(), type);
        graphVo.setWordList(wordList);
        graphVo.setLabelList(labelList);
        HttpEntity<ModelResult> entity = new HttpEntity<>(graphVo, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AI_SERVER_URL + copyModelData, entity, String.class);
        String res = responseEntity.getBody();
        return responseEntity.getBody();
    }

}
