package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-11 20:56
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class RelationTypeAddRequest {

    /**
     * 头实体的在item表中的id
     */
    private Long startEntityItemId;

    /**
     * 尾实体的在item表中的id
     */
    private Long endEntityItemId;

    /**
     * 关系的名称
     */
    private String relationName;


}
