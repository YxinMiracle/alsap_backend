package com.yxinmiracle.alsap.aiService;

/*
 * @author  YxinMiracle
 * @date  2023-09-20 10:11
 * @Gitee: https://gitee.com/yxinmiracle
 */


import cn.hutool.json.JSONUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.model.ModelResult;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.service.ItemService;
import com.yxinmiracle.alsap.service.RelationTypeService;
import com.yxinmiracle.alsap.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiServer {

    @Resource
    private CtiService ctiService;

    @Resource
    private RelationTypeService relationTypeService;

    @Resource
    private ItemService itemService;

    private static final String AUTH_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Value("${aiServer.host}")
    public static String AI_SERVER_URL;

    public static String ttpServerPath = "/annotation/cti";
    public static String getGraphServePath = "/graph/cti";
    public static String copyModelData = "/copy";


    /**
     * 输入的信息是Cti情报的内容
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

    public String pythonCreateGraph(Cti  cti) {
        // 这里添加完之后，需要调用ai服务，进行模型的抽取
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ModelResult graphVo = new ModelResult();
        graphVo.setId(cti.getId());

        Type type = new TypeToken<List<List<String>>>() {}.getType();
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
        Type type = new TypeToken<List<List<String>>>() {}.getType();
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
