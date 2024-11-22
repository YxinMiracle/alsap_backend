package com.yxinmiracle.alsap.rule.impl;

/*
 * @author  YxinMiracle
 * @date  2024-10-30 12:57
 * @Gitee: https://gitee.com/yxinmiracle
 * 处理创建规则功能
 */

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.common.AiServerRet;
import com.yxinmiracle.alsap.manager.CosManager;
import com.yxinmiracle.alsap.manager.MakerManager;
import com.yxinmiracle.alsap.model.entity.*;
import com.yxinmiracle.alsap.model.enums.FileUploadBizEnum;
import com.yxinmiracle.alsap.model.enums.LlmStatusEnum;
import com.yxinmiracle.alsap.model.enums.RuleFileStatusEnum;
import com.yxinmiracle.alsap.rule.RuleFunBox;
import com.yxinmiracle.alsap.rule.meta.TripleMate;
import com.yxinmiracle.alsap.rule.meta.YaraMate;
import com.yxinmiracle.alsap.rule.meta.YaraMetaValidator;
import com.yxinmiracle.alsap.rule.model.CtiRuleRequest;
import com.yxinmiracle.alsap.rule.model.vo.CreateCtiYaraVo;
import com.yxinmiracle.alsap.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

// Yara规则创建
@Service
@Slf4j
public class YaraRuleCreateFunImpl implements RuleFunBox {

    @Resource
    private ItemService itemService;

    @Resource
    private RelationTypeService relationTypeService;

    @Resource
    private RelationService relationService;

    @Resource
    private EntityService entityService;

    @Resource
    private CtiTtpsService ctiTtpsService;

    @Resource
    private AiServer aiServer;

    @Resource
    private CtiRulesService ctiRulesService;

    @Resource
    private MakerManager makerManager;

    @Resource
    private CosManager cosManager;

    private static final List<String> keywords = Arrays.asList("file-hash", "malware", "file-name", "windows-registry-key", "process");

    /**
     * 判断是否存在已经定义好的内容
     *
     * @param input
     * @return
     */
    public boolean containsAnyKeyword(String input) {
        return keywords.stream().anyMatch(input::contains);
    }

    /**
     * 在这个地方我需根据我们获取的ctiId获取相关的信息，例如实体信息，实体之间的关系，以及关于该情报的ttp数据，然后调用Ai服务获取yara规则的相关数据
     * 获取了之后进行存储，然后存储到存储桶中
     *
     * @param request
     * @return
     */
    @Override
    public void execute(CtiRuleRequest request) {
        Long ctiId = request.getCtiId();

        List<RelationType> relationTypeList = relationTypeService.list();
        List<Entity> entityList = entityService.list(new LambdaQueryWrapper<Entity>().eq(Entity::getCtiId, ctiId));

        Map<Long, Entity> entityMap = entityList.stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        Map<Long, RelationType> relationTypeMap = relationTypeList.stream().collect(Collectors.toMap(RelationType::getId, Function.identity()));
        List<Relation> dbRelationList = relationService.list(new LambdaQueryWrapper<Relation>().eq(Relation::getCtiId, ctiId));

        Map<Long, Item> itemId2ItemMap = itemService.getItemId2ItemMap();

        List<TripleMate> tripleMateList = new ArrayList<>();


        for (Relation relation : dbRelationList) {

            Long relationTypeId = relation.getRelationTypeId();
            RelationType relationType = relationTypeMap.get(relationTypeId);
            String relationName = relationType.getRelationName(); // 获取关系的名称

            // 获取头实体名称
            Long startCtiEntityId = relation.getStartCtiEntityId();
            if (startCtiEntityId == null) continue;
            Entity startEntity = entityMap.get(startCtiEntityId);
            String startItemName = itemId2ItemMap.get(startEntity.getItemId()).getItemName();
            String startEntityName = startEntity.getEntityName();

            // 获取尾实体名称
            Long endCtiEntityId = relation.getEndCtiEntityId();
            if (endCtiEntityId == null) continue;
            Entity endEntity = entityMap.get(endCtiEntityId);
            String endItemName = itemId2ItemMap.get(endEntity.getItemId()).getItemName();
            String endEntityName = endEntity.getEntityName();

            TripleMate tripleMate = extracted(relationName, startEntityName, startItemName, endEntityName, endItemName);
            tripleMateList.add(tripleMate);
        }

        Map<String, List<String>> iocMap = new HashMap<>();

        // file-hash、malware、file-name、windows-registry-key、process
        // 选出对应的ioc
        for (Entity entity : entityList) {
            String itemName = itemId2ItemMap.get(entity.getItemId()).getItemName();
            if (containsAnyKeyword(itemName)) {
                iocMap.putIfAbsent(itemName, new ArrayList<>());
                iocMap.get(itemName).add(entity.getEntityName());
            }
        }

        String articleLevelTtp = Optional.ofNullable(
                        ctiTtpsService.getOne(new LambdaQueryWrapper<CtiTtps>().eq(CtiTtps::getCtiId, ctiId))
                ).map(CtiTtps::getArticleLevelTtp)
                .orElse("none");

        CreateCtiYaraVo createCtiYaraVo = new CreateCtiYaraVo();
        createCtiYaraVo.setIocData(JSONUtil.toJsonPrettyStr(iocMap));
        createCtiYaraVo.setRelationGraph(JSONUtil.toJsonPrettyStr(tripleMateList));

        // 请求之前调用异步方法
        CompletableFuture.runAsync(() -> {
            LambdaUpdateWrapper<CtiRules> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CtiRules::getCtiId, ctiId)
                    .eq(CtiRules::getSingleRequestId, request.getRequestId())
                    .set(CtiRules::getLlmStatus, LlmStatusEnum.REQUESTED.getValue());
            log.info("向ai服务器发起请求，请求id为: {%s}", request.getRequestId());
            ctiRulesService.update(updateWrapper);
        });

        CtiRules ctiRulesInDb = ctiRulesService.getOne(new LambdaQueryWrapper<CtiRules>().eq(CtiRules::getCtiId, ctiId).eq(CtiRules::getSingleRequestId, request.getRequestId()));

        AiServerRet ctiYaraRule = aiServer.createCtiYaraRule(createCtiYaraVo);
        try {
            YaraMate yaraMate = JSONUtil.toBean(ctiYaraRule.getData(), YaraMate.class);
            yaraMate.setReference("/cti/show/detail/" + ctiId);
            YaraMetaValidator.doValidAndFill(yaraMate);
            ctiRulesInDb.setLlmStatus(LlmStatusEnum.RESPONSE_RECEIVED.getValue());
            ctiRulesInDb.setLlmResult(JSONUtil.toJsonPrettyStr(yaraMate));

            // 生成路径默认值
            String projectPath = System.getProperty("user.dir");
            String inputPath = projectPath + File.separator + "src/main/resources/templates/rule/TemplateYaraRule.yar.ftl";
            String saveFileName = request.getCtiId() + "-" + request.getRequestId();
            String outputPath = String.format("%s/.temp/rules/%s.yar", projectPath, saveFileName);

            makerManager.doGenerate(inputPath, outputPath, yaraMate);
            File yaraRuleFilePath = new File(outputPath);
            String uploadFilePath = String.format("/%s/%s/%s.yar", FileUploadBizEnum.YARA_RULE.getValue(), ctiId, saveFileName);
            cosManager.putObject(uploadFilePath, yaraRuleFilePath);

            ctiRulesInDb.setRuleFileStatus(RuleFileStatusEnum.GENERATED.getValue());
            ctiRulesInDb.setFilePath(uploadFilePath);
            ctiRulesInDb.setRuleName(yaraMate.getRuleName());
            ctiRulesInDb.setRuleDescription(yaraMate.getDescription());

            log.info("向ai服务器发起请求，请求id为: {%s}, 请求成功", request.getRequestId());
        } catch (Exception e) {
            ctiRulesInDb.setLlmStatus(LlmStatusEnum.RESPONSE_ERROR.getValue());
            ctiRulesInDb.setRuleFileStatus(RuleFileStatusEnum.NOT_GENERATED.getValue());
            e.printStackTrace();
            log.info("向ai服务器发起请求，请求id为: {%s}, 请求失败", request.getRequestId());
        }

        ctiRulesService.updateById(ctiRulesInDb);
    }

    private static TripleMate extracted(String relationName, String startEntityName, String startItemName, String endEntityName, String endItemName) {
        TripleMate tripleMate = new TripleMate();
        tripleMate.setRelation(relationName);

        TripleMate.SubjectData headObject = new TripleMate.SubjectData();
        headObject.setName(startEntityName);
        headObject.setType(startItemName);
        tripleMate.setSubject(headObject);

        TripleMate.SubjectData endObject = new TripleMate.SubjectData();
        endObject.setName(endEntityName);
        endObject.setType(endItemName);
        tripleMate.setObject(endObject);

        return tripleMate;
    }
}
