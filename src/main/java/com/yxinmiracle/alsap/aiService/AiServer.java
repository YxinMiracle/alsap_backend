package com.yxinmiracle.alsap.aiService;

/*
 * @author  YxinMiracle
 * @date  2023-09-20 10:11
 * @Gitee: https://gitee.com/yxinmiracle
 */


import cn.hutool.json.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.model.dto.RetrainRequest;
import com.yxinmiracle.alsap.model.dto.cti.ModelAddTestRequest;
import com.yxinmiracle.alsap.model.dto.cti.NerModelDto;
import com.yxinmiracle.alsap.model.dto.model.ModelCtiVo;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.service.ItemService;
import com.yxinmiracle.alsap.service.RelationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;

@SpringBootTest
public class AiServer {

    @Resource
    private CtiService ctiService;

    @Resource
    private RelationTypeService relationTypeService;

    @Resource
    private ItemService itemService;

    public static String AI_SERVER_URL = "http://model.yxinmiracle.com:8365";

    public static String ttpServerPath = "/annotation/cti";
    public static String getGraphServePath = "/graph/cti";
    public static String copyModelData = "/copy";
    public static String nerTestModel = "/test/content";
    public static String modelRetrain = "/retrain";


    public static ModelCtiVo getEntityAns(String ctiContent) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求数据
        Cti ctiObj = new Cti();
        ctiObj.setContent(ctiContent);

        // 开始请求
        HttpEntity<Cti> entity = new HttpEntity<Cti>(ctiObj, headers);
        ResponseEntity<ModelCtiVo> responseEntity = restTemplate.postForEntity(AI_SERVER_URL + ttpServerPath, entity, ModelCtiVo.class);

        return responseEntity.getBody();
    }

    public String pythonCreateGraph(Cti  cti) {
        // 这里添加完之后，需要调用ai服务，进行模型的抽取
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ModelCtiVo graphVo = new ModelCtiVo();
        graphVo.setId(cti.getId());
        Type type = new TypeToken<List<List<String>>>() {}.getType();
        Gson gson = new Gson();
        List<List<String>> wordList = gson.fromJson(cti.getWordList(), type);
        List<List<String>> labelList = gson.fromJson(cti.getLabelList(), type);
        graphVo.setWordList(wordList);
        graphVo.setLabelList(labelList);
        HttpEntity<ModelCtiVo> entity = new HttpEntity<>(graphVo, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AI_SERVER_URL + getGraphServePath, entity, String.class);
        String res = responseEntity.getBody();
        return responseEntity.getBody();
    }

    public String copyModelData(Cti cti) {
        // 这里添加完之后，需要调用ai服务，进行模型的抽取
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ModelCtiVo graphVo = new ModelCtiVo();
        graphVo.setId(cti.getId());
        Type type = new TypeToken<List<List<String>>>() {}.getType();
        Gson gson = new Gson();
        List<List<String>> wordList = gson.fromJson(cti.getWordList(), type);
        List<List<String>> labelList = gson.fromJson(cti.getLabelList(), type);
        graphVo.setWordList(wordList);
        graphVo.setLabelList(labelList);
        HttpEntity<ModelCtiVo> entity = new HttpEntity<>(graphVo, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(AI_SERVER_URL + copyModelData, entity, String.class);
        String res = responseEntity.getBody();
        return responseEntity.getBody();
    }

    // 判断模型的好坏，这里是测试模型
    public static NerModelDto nerModelTest(ModelAddTestRequest dto) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ModelAddTestRequest> entity = new HttpEntity<ModelAddTestRequest>(dto, headers);

        // 使用fastJson将ai服务返回回来的json字符串转为jsonList对象
        ResponseEntity<NerModelDto> nerModelDto = restTemplate.postForEntity(AI_SERVER_URL + nerTestModel, entity, NerModelDto.class);
        System.out.println();
        return nerModelDto.getBody();
    }

    // 判断模型的好坏，这里是测试模型
    public static void modelRetrain(RetrainRequest retrainRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RetrainRequest> entity = new HttpEntity<RetrainRequest>(retrainRequest, headers);

        // 使用fastJson将ai服务返回回来的json字符串转为jsonList对象
        ResponseEntity<String> nerModelDto = restTemplate.postForEntity(AI_SERVER_URL + modelRetrain, entity, String.class);
    }

}
