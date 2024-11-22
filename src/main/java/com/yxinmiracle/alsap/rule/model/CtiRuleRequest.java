package com.yxinmiracle.alsap.rule.model;

/*
 * @author  YxinMiracle
 * @date  2024-07-04 13:36
 * @Gitee: https://gitee.com/yxinmiracle
 */


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtiRuleRequest {
    private String processRuleName; // 需要处理的规则名称
    private String requestId;
    private Long ctiId;
}
