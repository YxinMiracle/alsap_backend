package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-11 20:56
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class RelationAddRequest {
    /**
     * 这个实体对应的CTI文章是什么
     */
    private Long ctiId;

    /**
     * 头实体Id
     */
    private Long startCtiEntityId;

    /**
     * 尾实体Id
     */
    private Long endCtiEntityId;

    /**
     * 关系Id
     */
    private Long relationTypeId;
}
