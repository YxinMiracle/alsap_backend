package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2023-10-02 17:15
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.entity.NerModelTestStep;
import lombok.Data;

@Data
public class NerModelTestStepResultVo extends NerModelTestStep {

    private String avgPrecision;
    private String avgRecall;
    private String avgF1Score;
    private short nerModelTestIsTest;
    private short nerModelTestIsRetrain;

}
