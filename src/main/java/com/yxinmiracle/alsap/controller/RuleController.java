package com.yxinmiracle.alsap.controller;

/*
 * @author  YxinMiracle
 * @date  2024-10-30 12:40
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.utils.IOUtils;
import com.yxinmiracle.alsap.annotation.AuthCheck;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.constant.UserConstant;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.manager.CosManager;
import com.yxinmiracle.alsap.manager.MakerManager;
import com.yxinmiracle.alsap.model.dto.rule.CtiRuleDeleteRequest;
import com.yxinmiracle.alsap.model.dto.rule.CtiRuleQueryRequest;
import com.yxinmiracle.alsap.model.entity.CtiRules;
import com.yxinmiracle.alsap.model.enums.LlmStatusEnum;
import com.yxinmiracle.alsap.model.enums.RuleFileStatusEnum;
import com.yxinmiracle.alsap.model.enums.RuleTypeEnum;
import com.yxinmiracle.alsap.rule.RuleFunBox;
import com.yxinmiracle.alsap.rule.RuleFunBoxFactory;
import com.yxinmiracle.alsap.rule.model.CtiRuleRequest;
import com.yxinmiracle.alsap.service.CtiRulesService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/rule")
@Slf4j
public class RuleController {
    @Resource
    private RuleFunBoxFactory ruleFunBoxFactory;

    @Resource
    private MakerManager makerManager;

    @Resource
    private CosManager cosManager;

    @Resource
    private CtiRulesService ctiRulesService;

    @PostMapping("/create")
    @ApiModelProperty(value = "根据CtiId创建对应的规则")
    @DecryptRequestBody
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> createRule(@RequestBody CtiRuleRequest ctiRuleRequest, HttpServletRequest request) {
        String processRuleName = ctiRuleRequest.getProcessRuleName();
        Long ctiId = ctiRuleRequest.getCtiId();

        if (StringUtils.isBlank(processRuleName) || ObjectUtils.isEmpty(ctiId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 根据规则的类型，获取对应的enum类，然后再根据enum类，通过工厂模式获取对应的实现类
        RuleTypeEnum ruleTypeEnum = RuleTypeEnum.getEnumByText(processRuleName);

        if (ruleTypeEnum == null) { // 确保这个请求是我们要求范围之内的
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 利用工厂模型进行获取执行类
        RuleFunBox ruleFunBox = ruleFunBoxFactory.newInstance(ruleTypeEnum);

        // 创建一个唯一请求id，并保存状态
        String requestId = UUID.randomUUID().toString();
        CtiRules ctiRules = new CtiRules();
        ctiRules.setCtiId(ctiId);
        ctiRules.setSingleRequestId(requestId);
        ctiRules.setRuleType(ruleTypeEnum.getValue());
        ctiRules.setLlmStatus(LlmStatusEnum.NOT_REQUESTED.getValue());
        ctiRules.setRuleFileStatus(RuleFileStatusEnum.NOT_GENERATED.getValue());
        ctiRulesService.save(ctiRules);


        // 去进行远程执行
        // 调用异步方法
        CompletableFuture.runAsync(() -> {
            ctiRuleRequest.setRequestId(requestId);
            ruleFunBox.execute(ctiRuleRequest);
        });

        return ResultUtils.success(true);
    }

    @PostMapping("/list")
    @ApiModelProperty(value = "根据CtiId获取对应的规则")
    @DecryptRequestBody
    public BaseResponse<List<CtiRules>> getRuleByCtiId(@RequestBody CtiRuleQueryRequest ctiRuleQueryRequest, HttpServletRequest request) {
        Long ctiId = ctiRuleQueryRequest.getCtiId();
        String ruleName = ctiRuleQueryRequest.getRuleName();

        if (ObjectUtils.isEmpty(ctiId) || ObjectUtils.isEmpty(ruleName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        RuleTypeEnum ruleTypeEnum = RuleTypeEnum.getEnumByText(ruleName);

        if (ruleTypeEnum == null) { // 确保这个请求是我们要求范围之内的
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<CtiRules> ctiRulesList = ctiRulesService.list(new LambdaQueryWrapper<CtiRules>().eq(CtiRules::getCtiId, ctiId).eq(CtiRules::getRuleType, ruleTypeEnum.getValue()).orderByDesc(CtiRules::getUpdateTime));

        return ResultUtils.success(ctiRulesList);
    }


    @PostMapping("/delete")
    @ApiModelProperty(value = "根据CtiId获取对应的yara规则")
    @DecryptRequestBody
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> removeRuleByRuleId(@RequestBody CtiRuleDeleteRequest ctiRuleDeleteRequest, HttpServletRequest request) {
        Long ruleId = ctiRuleDeleteRequest.getRuleId();

        if (ObjectUtils.isEmpty(ruleId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean b = ctiRulesService.removeById(ruleId);

        return ResultUtils.success(b);
    }


    @GetMapping("/download")
    public void downloadRuleById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        CtiRules ctiRules = ctiRulesService.getOne(new LambdaQueryWrapper<CtiRules>().eq(CtiRules::getId, id));

        if (ctiRules == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String filePath = ctiRules.getFilePath();
        if (StringUtils.isBlank(filePath)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "规则文件不存在");
        }

        // 日志记录
        log.info("用户下载了 {}", filePath);

        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filePath);

        InputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObj(filePath);
            cosObjectInput = cosObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            // 写入相应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 用完流之后一定要调用 close()
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

}
