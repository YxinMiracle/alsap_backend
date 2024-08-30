package com.yxinmiracle.alsap.model.dto.relationType;

/*
 * @author  YxinMiracle
 * @date  2024-08-29 0:09
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "关系类型查询Dto")
public class RelationTypeQueryRequest extends PageRequest implements Serializable {


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
