package com.yxinmiracle.alsap.rule.impl;

/*
 * @author  YxinMiracle
 * @date  2024-10-30 12:57
 * @Gitee: https://gitee.com/yxinmiracle
 * 处理更新规功能
 */

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.common.AiServerRet;
import com.yxinmiracle.alsap.manager.CosManager;
import com.yxinmiracle.alsap.manager.MakerManager;
import com.yxinmiracle.alsap.model.entity.CtiRules;
import com.yxinmiracle.alsap.model.entity.Entity;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.enums.FileUploadBizEnum;
import com.yxinmiracle.alsap.model.enums.LlmStatusEnum;
import com.yxinmiracle.alsap.model.enums.RuleFileStatusEnum;
import com.yxinmiracle.alsap.rule.RuleFunBox;
import com.yxinmiracle.alsap.rule.meta.SnortMate;
import com.yxinmiracle.alsap.rule.model.CtiRuleRequest;
import com.yxinmiracle.alsap.rule.model.vo.CreateCtiSnortVo;
import com.yxinmiracle.alsap.service.CtiRulesService;
import com.yxinmiracle.alsap.service.EntityService;
import com.yxinmiracle.alsap.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

// Snort规则更新
@Service
@Slf4j
public class SnortRuleCreateFunImpl implements RuleFunBox {

    @Resource
    private EntityService entityService;

    @Resource
    private ItemService itemService;

    @Resource
    private CtiRulesService ctiRulesService;

    @Resource
    private AiServer aiServer;

    @Resource
    private MakerManager makerManager;

    @Resource
    private CosManager cosManager;

    // 需要筛选出来的类型
    private static final List<String> keywords = Arrays.asList("domain-name", "ipv4-addr", "ipv6-addr");

    /**
     * 判断是否存在已经定义好的内容
     *
     * @param input
     * @return
     */
    public boolean containsAnyKeyword(String input) {
        return keywords.stream().anyMatch(input::contains);
    }

    @Override
    public void execute(CtiRuleRequest request) {

        Long ctiId = request.getCtiId();

        List<Entity> entityList = entityService.list(new LambdaQueryWrapper<Entity>().eq(Entity::getCtiId, ctiId));
        Map<Long, Entity> entityMap = entityList.stream().collect(Collectors.toMap(Entity::getId, Function.identity()));

        Map<Long, Item> itemId2ItemMap = itemService.getItemId2ItemMap();
        Map<String, List<String>> iocMap = new HashMap<>();

        // "domain-name", "ipv4-addr", "ipv6-addr"
        // 选出对应的ioc
        for (Entity entity : entityList) {
            String itemName = itemId2ItemMap.get(entity.getItemId()).getItemName();
            if (containsAnyKeyword(itemName)) {
                iocMap.putIfAbsent(itemName, new ArrayList<>());
                iocMap.get(itemName).add(entity.getEntityName());
            }
        }

        CreateCtiSnortVo createCtiSnortVo = new CreateCtiSnortVo();
        createCtiSnortVo.setIocData(JSONUtil.toJsonPrettyStr(iocMap));
        System.out.println(JSONUtil.toJsonPrettyStr(iocMap));

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

        AiServerRet ctiSnortRule = aiServer.createCtiSnortRule(createCtiSnortVo);
        try {
            SnortMate snortMate = JSONUtil.toBean(ctiSnortRule.getData(), SnortMate.class);
            ctiRulesInDb.setRuleDescription(snortMate.getDescription());
            ctiRulesInDb.setRuleName(snortMate.getRuleName());
            ctiRulesInDb.setLlmStatus(LlmStatusEnum.RESPONSE_RECEIVED.getValue());
            ctiRulesInDb.setLlmResult(JSONUtil.toJsonPrettyStr(snortMate));

            // 生成路径默认值
            String projectPath = System.getProperty("user.dir");
            String inputPath = projectPath + File.separator + "src/main/resources/templates/rule/TemplateSnortRule.rules.ftl";
            String saveFileName = request.getCtiId() + "-" + request.getRequestId();
            String outputPath = String.format("%s/.temp/rules/%s.rules", projectPath, saveFileName);

            makerManager.doGenerate(inputPath, outputPath, snortMate);
            File yaraRuleFilePath = new File(outputPath);
            String uploadFilePath = String.format("/%s/%s/%s.rules", FileUploadBizEnum.SNORT_RULE.getValue(), ctiId, saveFileName);
            cosManager.putObject(uploadFilePath, yaraRuleFilePath);

            ctiRulesInDb.setRuleFileStatus(RuleFileStatusEnum.GENERATED.getValue());
            ctiRulesInDb.setFilePath(uploadFilePath);

            log.info("向ai服务器发起请求，请求id为: {%s}, 请求成功", request.getRequestId());
        } catch (Exception e) {
            ctiRulesInDb.setLlmStatus(LlmStatusEnum.RESPONSE_ERROR.getValue());
            ctiRulesInDb.setRuleFileStatus(RuleFileStatusEnum.NOT_GENERATED.getValue());
            e.printStackTrace();
            log.info("向ai服务器发起请求，请求id为: {%s}, 请求失败", request.getRequestId());
        }

        ctiRulesService.updateById(ctiRulesInDb);

    }


}
