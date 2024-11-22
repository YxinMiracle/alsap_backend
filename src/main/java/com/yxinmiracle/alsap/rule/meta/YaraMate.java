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
public class YaraMate {

    private String ruleName; // 规则名称
    private String description; // 规则描述
    private String author; // 规则描述
    private String reference; // 相关引用 可以存放是从哪个cti来的
    private String createDate; // 创建时间

    // =======================================
    private List<Variable> variableList; // 存储变量信息

    @NoArgsConstructor
    @Data
    public static class Variable {
        private String variableName;
        private String variableValue;
    }

    private String condition; // 存储条件

}
