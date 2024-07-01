package com.yxinmiracle.alsap.schedule;

/*
 * @author  YxinMiracle
 * @date  2023-10-03 22:21
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.model.dto.RetrainRequest;
import com.yxinmiracle.alsap.model.entity.NerModelTest;
import com.yxinmiracle.alsap.model.entity.NerModelTestStep;
import com.yxinmiracle.alsap.service.NerModelTestStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@EnableScheduling//开启定时任务
@Component
public class ScheduledTask {

//    @Resource
//    public NerModelTestStepService nerModelTestStepService;
//
//
////    @Scheduled(cron = "0 0 */2 * * ?")
//    @Scheduled(cron = "*/500 * * * * ?")
//    public void is_retrain() {
//        List<NerModelTestStep> list = nerModelTestStepService.list(new LambdaQueryWrapper<NerModelTestStep>().eq(NerModelTestStep::getNerModelTestIsRetrain, NerModelTestStep.IsRetrainTypeEnum.IsRetrain.getCode())
//                .eq(NerModelTestStep::getNerModelTestIsTest, NerModelTestStep.IsTestTypeEnum.NotTest.getCode()));
//        if (list.size() > 10) {
//            RetrainRequest retrainRequest = new RetrainRequest();
//            StringBuilder tempStr = new StringBuilder();
//            for (NerModelTestStep nerModelTestStep : list) {
//                tempStr.append(nerModelTestStep.getNerModelTestContent());
//            }
//            retrainRequest.setTrainData(tempStr.toString());
//            AiServer.modelRetrain(retrainRequest);
//        }
//        System.out.println("正在执行计时任务");
//    }
}


