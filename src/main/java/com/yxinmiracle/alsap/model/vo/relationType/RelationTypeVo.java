package com.yxinmiracle.alsap.model.vo.relationType;

/*
 * @author  YxinMiracle
 * @date  2024-08-28 23:54
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "关系类型实体Vo")
public class RelationTypeVo implements Serializable {

    @ApiModelProperty("关系类型id")
    Long id;

    @ApiModelProperty("实体之间的关系名称")
    String relationName;

    @ApiModelProperty("头节点Item的id")
    Long startItemId;

    @ApiModelProperty("头节点Item名称")
    String startItemName;

    @ApiModelProperty("头节点Item类型")
    Integer startItemType;

    @ApiModelProperty("尾节点ItemId")
    Long endItemId;

    @ApiModelProperty("尾节点名称")
    String endItemName;

    @ApiModelProperty("尾节点类型")
    Integer endItemType;
}
