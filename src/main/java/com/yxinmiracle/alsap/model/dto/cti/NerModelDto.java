package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2023-10-02 12:20
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.entity.NerModelTest;
import com.yxinmiracle.alsap.model.entity.NerModelTestResult;
import lombok.Data;

import java.util.List;

@Data
public class NerModelDto {

    private List<NerModelTest> nerModelTestList;
    private List<NerModelTestResult> nerModelTestResults;
    private int sentNum;
}
