package com.yxinmiracle.alsap.rule.model;

/*
 * @author  YxinMiracle
 * @date  2024-07-04 13:36
 * @Gitee: https://gitee.com/yxinmiracle
 */


import com.yxinmiracle.alsap.rule.meta.YaraMate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtiRuleResponse {
    private YaraMate yaraMate;
}
