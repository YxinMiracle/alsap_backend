package com.yxinmiracle.alsap.model.dto.rule;

/*
 * @author  YxinMiracle
 * @date  2024-11-01 15:51
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class CtiRuleDeleteRequest implements Serializable {
    Long ruleId;
}
