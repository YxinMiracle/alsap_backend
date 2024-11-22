package com.yxinmiracle.alsap.rule.meta;

/*
 * @author  YxinMiracle
 * @date  2024-10-30 17:46
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@Data
public class SnortMate {

    private String ruleName; // 规则名称
    private String description; // 规则描述
    private List<SnortRule> snortRuleList; // 规则描述

    @NoArgsConstructor
    @Data
    public static class SnortRule {
        private String protocol;
        private String destination;
        private String msg;
        private String sid;
        private String rev;
    }


}
