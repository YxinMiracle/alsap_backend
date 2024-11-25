package com.yxinmiracle.alsap.model.db;

/*
 * @author  YxinMiracle
 * @date  2024-11-23 18:04
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class CtiRelationRuleCount {

    private Long ctiId;
    private Integer ctiRuleCount;
    private Integer relationCount;

}
