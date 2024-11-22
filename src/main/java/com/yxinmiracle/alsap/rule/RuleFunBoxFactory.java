package com.yxinmiracle.alsap.rule;

/*
 * @author  YxinMiracle
 * @date  2024-10-30 12:56
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.enums.RuleTypeEnum;
import com.yxinmiracle.alsap.rule.impl.SnortRuleCreateFunImpl;
import com.yxinmiracle.alsap.rule.impl.YaraRuleCreateFunImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RuleFunBoxFactory {

    @Resource
    private YaraRuleCreateFunImpl yaraRuleCreateFunImpl;

    @Resource
    private SnortRuleCreateFunImpl snortRuleCreateFunImpl;

    public RuleFunBox newInstance(RuleTypeEnum ruleTypeEnum) {
        switch (ruleTypeEnum) {
            case YARA:
                return yaraRuleCreateFunImpl;
            case SNORT:
                return snortRuleCreateFunImpl;
            default:
                return yaraRuleCreateFunImpl;
        }
    }


}
