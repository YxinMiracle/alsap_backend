package com.yxinmiracle.alsap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.model.dto.cti.ModelAddTestRequest;
import com.yxinmiracle.alsap.model.dto.cti.NerModelDto;
import com.yxinmiracle.alsap.model.entity.NerModelTest;
import com.yxinmiracle.alsap.model.entity.NerModelTestResult;
import com.yxinmiracle.alsap.model.entity.NerModelTestStep;
import com.yxinmiracle.alsap.model.vo.NerModelTestStepResultVo;
import com.yxinmiracle.alsap.service.NerModelTestResultService;
import com.yxinmiracle.alsap.service.NerModelTestService;
import com.yxinmiracle.alsap.service.NerModelTestStepService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * @author  YxinMiracle
 * @date  2023-10-01 22:02
 * @Gitee: https://gitee.com/yxinmiracle
 */

@RestController
@RequestMapping("/aiNerModelTest")
@Slf4j
public class AiNerModelTestController {

    @Resource
    public NerModelTestStepService nerModelTestStepService;

    @Resource
    public NerModelTestService nerModelTestService;

    @Resource
    public NerModelTestResultService nerModelTestResultService;


    public int getNerModelTestStepNumInDB() {
        List<NerModelTestStep> list = nerModelTestStepService.list();
        return list.size() + 1;
    }

    @PostMapping("/addTest")
    public BaseResponse<Long> addCtiReport(@RequestBody ModelAddTestRequest modelAddTestRequest, HttpServletRequest request) {
        if (modelAddTestRequest == null || StringUtils.isBlank(modelAddTestRequest.getNerTestData())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        NerModelDto nerModelDto;
        // 添加AI服务异常捕获信息
        try {
            nerModelDto = AiServer.nerModelTest(modelAddTestRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
        }
        // 这里获取到了对应的结果

        int sentNum = nerModelDto.getSentNum();
        NerModelTestStep nerModelTestStep = new NerModelTestStep();
        nerModelTestStep.setNerModelTestContent(modelAddTestRequest.getNerTestData());
        nerModelTestStep.setNerModelTestCreateTime(new Date());
        nerModelTestStep.setStepNum(getNerModelTestStepNumInDB());
        nerModelTestStep.setNerModelTestSentNum(sentNum);
        nerModelTestStepService.save(nerModelTestStep);


        List<NerModelTest> nerModelTestList = nerModelDto.getNerModelTestList();
        List<String> stringList = new ArrayList<>();
        for (NerModelTest nerModelTest : nerModelTestList) {
            System.out.println(nerModelTest);
            if (nerModelTest.getNerModelTestTypeName().equals("micro avg")) {  // 获取对应的F1分数
                // 找到了对应的行
                stringList.add(nerModelTest.getNerModelTestF1Score());
            }
            nerModelTest.setNerModelTestStepId(nerModelTestStep.getNerModelTestStepId());
            nerModelTestService.save(nerModelTest);
        }
        String nerModelTestF1Score = stringList.get(0);
        float f1Score = Float.parseFloat(nerModelTestF1Score);
        if (f1Score != Float.parseFloat("1.0000")) {
            // 如果说不等于的话，那么就表示需要重新进行训练
            nerModelTestStep.setNerModelTestIsRetrain(NerModelTestStep.IsRetrainTypeEnum.IsRetrain.getCode()); // 表示需要进行徐连
            nerModelTestStep.setNerModelTestIsTest(NerModelTestStep.IsTestTypeEnum.NotTest.getCode()); // 表示还没有训练过
        } else {
            // 那么就是等于1，表示的是全部正确
            nerModelTestStep.setNerModelTestIsRetrain(NerModelTestStep.IsRetrainTypeEnum.NotRetrain.getCode()); // 就设定为不需要重新进行训练
            nerModelTestStep.setNerModelTestIsTest(NerModelTestStep.IsTestTypeEnum.DontTest.getCode()); // -1 寿命不需要进行预训练
        }
        LambdaUpdateWrapper<NerModelTestStep> nerModelTestStepLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        nerModelTestStepLambdaUpdateWrapper.eq(NerModelTestStep::getNerModelTestStepId, nerModelTestStep.getNerModelTestStepId());
        nerModelTestStepService.update(nerModelTestStep, nerModelTestStepLambdaUpdateWrapper); // 根据id进行更行

        // 3. 对于每句话中测试的结果
        List<NerModelTestResult> nerModelTestResults = nerModelDto.getNerModelTestResults();
        for (NerModelTestResult nerModelTestResult : nerModelTestResults) {
            nerModelTestResult.setNerModelTestStepId(nerModelTestStep.getNerModelTestStepId());
            nerModelTestResultService.save(nerModelTestResult);
        }
        return ResultUtils.success(1L);
    }

    @GetMapping("/data")
    public BaseResponse<List<NerModelTestStepResultVo>> getNerTestResultList() {
        // 先查询出一共有几次，把次数的列表找出来
        List<NerModelTestStepResultVo> dbResult = new ArrayList<>();
        for (NerModelTestStep nerModelTestStep : nerModelTestStepService.list()) {
            Integer nerModelTestStepId = nerModelTestStep.getNerModelTestStepId();

            LambdaQueryWrapper<NerModelTest> nerModelTestLambdaQueryWrapper = new LambdaQueryWrapper<>();
            nerModelTestLambdaQueryWrapper.eq(NerModelTest::getNerModelTestTypeName, "micro avg");
            nerModelTestLambdaQueryWrapper.eq(NerModelTest::getNerModelTestStepId, nerModelTestStepId);
            NerModelTest nerModelTest = nerModelTestService.list(nerModelTestLambdaQueryWrapper).get(0);

            NerModelTestStepResultVo temp = new NerModelTestStepResultVo();
            temp.setAvgPrecision(nerModelTest.getNerModelTestPrecision());
            temp.setAvgF1Score(nerModelTest.getNerModelTestF1Score());
            temp.setAvgRecall(nerModelTest.getNerModelTestRecall());
            temp.setNerModelTestIsTest(nerModelTestStep.getNerModelTestIsTest());
            temp.setNerModelTestIsRetrain(nerModelTestStep.getNerModelTestIsRetrain());
            BeanUtils.copyProperties(nerModelTestStep, temp);
            dbResult.add(temp);
        }

        return ResultUtils.success(dbResult);
    }

    @GetMapping("/modelScore/{stepId}")
    public BaseResponse<List<List<NerModelTest>>> getNerModelScore(@PathVariable("stepId") Long nerModelTestStepId) {
        // 根据nerModelTestStepId获取模型的分数数据
        List<List<NerModelTest>> res = new ArrayList<>();
        LambdaQueryWrapper<NerModelTest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(NerModelTest::getNerModelTestStepId, nerModelTestStepId);
        res.add(nerModelTestService.list(lambdaQueryWrapper));
        return ResultUtils.success(res);
    }

    @GetMapping("/wordResult/{stepId}")
    public BaseResponse<List<List<NerModelTestResult>>> getNerModelWordResult(@PathVariable("stepId") Long nerModelTestStepId) {
        List<List<NerModelTestResult>> res = new ArrayList<>();
        LambdaQueryWrapper<NerModelTestResult> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(NerModelTestResult::getNerModelTestStepId, nerModelTestStepId);
        res.add(nerModelTestResultService.list(lambdaQueryWrapper));
        return ResultUtils.success(res);
    }

    @GetMapping("/update/retraindata")
    public BaseResponse<Boolean> updateRetrainData() {
        LambdaUpdateWrapper<NerModelTestStep> eq = new LambdaUpdateWrapper<NerModelTestStep>().eq(NerModelTestStep::getNerModelTestIsRetrain, NerModelTestStep.IsRetrainTypeEnum.IsRetrain.getCode())
                .eq(NerModelTestStep::getNerModelTestIsTest, NerModelTestStep.IsTestTypeEnum.NotTest.getCode())
                .set(NerModelTestStep::getNerModelTestIsRetrain, NerModelTestStep.IsRetrainTypeEnum.IsRetrain.getCode())
                .set(NerModelTestStep::getNerModelTestIsTest, NerModelTestStep.IsTestTypeEnum.IsTest.getCode());
        nerModelTestStepService.update(eq);
        return ResultUtils.success(true);
    }


}
