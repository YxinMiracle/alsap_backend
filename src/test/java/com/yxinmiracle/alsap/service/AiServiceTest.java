package com.yxinmiracle.alsap.service;

/*
 * @author  YxinMiracle
 * @date  2024-05-13 13:06
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.mapper.CtiMapper;
import com.yxinmiracle.alsap.model.dto.cti.PreventEntityQuery;
import com.yxinmiracle.alsap.model.dto.model.ModelCtiVo;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.entity.Relation;
import com.yxinmiracle.alsap.model.entity.RelationType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;

@SpringBootTest
public class AiServiceTest {

    @Resource
    private CtiService ctiService;

    @Resource
    private RelationTypeService relationTypeService;

    @Resource
    private ItemService itemService;

    @Resource
    private CtiMapper ctiMapper;

    @Resource
    private RelationService relationService;

    public static String AI_SERVER_URL = "http://172.22.105.24:5000";

    public static String ttpServerPath = "/annotation/cti";
    public static String getGraphServePath = "/graph/cti";

    @Test
    public void getEntityAns() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Cti ctiObj = ctiService.getById("1789287361180438530");

        HttpEntity<Cti> entity = new HttpEntity<Cti>(ctiObj, headers);
        ResponseEntity<ModelCtiVo> responseEntity = restTemplate.postForEntity(AI_SERVER_URL + ttpServerPath, entity, ModelCtiVo.class);
        ModelCtiVo modelCtiVo = responseEntity.getBody();
//        Gson gson = new Gson();
//        ModelCtiVo modelCtiVo = gson.fromJson(jsonString, ModelCtiVo.class);
        System.out.println(modelCtiVo.getId());
        // 这个存到模型实体表里面是为了展示模型对于每个单词的实际的识别结果，以及是为了后面点击生成图所展示的东西
        Gson gson = new Gson();
        List<List<String>> labelList = modelCtiVo.getLabelList();
        String jsonLabelListSting = gson.toJson(labelList);
        List<List<String>> ctiVoWordList = modelCtiVo.getWordList();
        String jsonWordListSting = gson.toJson(ctiVoWordList);
        Cti cti = new Cti();
        cti.setId(1790295677960581121L);
        cti.setWordList(jsonWordListSting);
        cti.setLabelList(jsonLabelListSting);
        ctiService.updateById(cti);
        // 使用fastJson将ai服务返回回来的json字符串转为jsonList对象
//        List<ArticleSentDto> resultList = JSONObject.parseArray(jsonString, ArticleSentDto.class);
//        return resultList;
    }

    @Test
    public void testPojo(){
        Cti cti = ctiService.getById(1789287825884155905L);
        Gson gson = new Gson();
        Type type = new TypeToken<List<List<String>>>() {}.getType();
        String wordList = cti.getWordList();
        List<List<String>> list = gson.fromJson(wordList, type);
        for (List<String> stringList : list) {
            System.out.println(stringList);
        }
    }

    @Test
    public void getGraphDataById(){

//        Gson gson = new Gson();
//        ModelCtiVo modelCtiVo = gson.fromJson(jsonString, ModelCtiVo.class);
        System.out.println("成功");
    }

    @Test
    public void getRelationType() {
        Map<Long, Item> map = new HashMap<>();
        List<RelationType> relationTypes = relationTypeService.list();
        List<Item> itemList = itemService.list();
        for (Item item : itemList) {
            map.put(item.getId(), item);
        }
        Map<String, List<String>> anlnaziyMap = new HashMap<>();
        for (RelationType relationType : relationTypes) {
            String relationName = relationType.getRelationName();
            String startEntityTypeName = map.get(relationType.getStartEntityItemId()).getItemName();
            String endEntityTypeName = map.get(relationType.getEndEntityItemId()).getItemName();

            String mapKey = startEntityTypeName+"|---|"+endEntityTypeName;

            if(anlnaziyMap.containsKey(mapKey)){
                // 如果有这个key
                List<String> strings = anlnaziyMap.get(mapKey);
                strings.add(relationName);
                anlnaziyMap.put(mapKey, strings);
            }else {
                List<String> stringList = new ArrayList<>();
                stringList.add(relationName);
                anlnaziyMap.put(mapKey, stringList);
            }
        }
//        for (Map.Entry<String, List<String>> stringListEntry : anlnaziyMap.entrySet()) {
//            String key = stringListEntry.getKey();
//            List<String> value = stringListEntry.getValue();
//        }
        // 创建 Gson 实例
        Gson gson = new Gson();
        // 将 Map 转换为 JSON 字符串
        String json = gson.toJson(anlnaziyMap);
        System.out.println(json);
//        System.out.println(anlnaziyMap);
    }

    public List<Relation> bfs(List<Relation> relationList, Long startId, int jumpNum) {
        Map<Long, List<Relation>> graph = new HashMap<>();
        List<Relation> result = new ArrayList<>();

        // 构建图，确保每个ID作为起点和终点都被处理
        for (Relation relation : relationList) {
            if (relation.getStartCtiEntityId() != null) {
                graph.computeIfAbsent(relation.getStartCtiEntityId(), k -> new ArrayList<>()).add(relation);
            }
            if (relation.getEndCtiEntityId() != null) {
                graph.computeIfAbsent(relation.getEndCtiEntityId(), k -> new ArrayList<>()).add(relation);
            }
        }

        // BFS
        Queue<Long> queue = new LinkedList<>();
        queue.offer(startId);
        Set<Long> visited = new HashSet<>();
        visited.add(startId);
        int currentLevel = 0;

        while (!queue.isEmpty() && (jumpNum == -1 || currentLevel <= jumpNum)) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Long currentId = queue.poll();

                if (!graph.containsKey(currentId)) {
                    continue;
                }

                for (Relation relation : graph.get(currentId)) {
                    Long neighborId = relation.getEndCtiEntityId().equals(currentId) ? relation.getStartCtiEntityId() : relation.getEndCtiEntityId();
                    if (!visited.contains(neighborId)) {
                        visited.add(neighborId);
                        queue.offer(neighborId);
                        result.add(relation);
                    }
                }
            }
            currentLevel++;
        }

        return result;
    }

    @Test
    public void testSearch(){
        // 给你一个节点，并且几阶的情况下
        List<Relation> relationList = relationService.list();
        List<Relation> bfs = bfs(relationList, 1789494340041588737L, -1);
        for (Relation bf : bfs) {
            System.out.println(bf);
        }
    }

}
