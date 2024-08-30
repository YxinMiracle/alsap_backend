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

@Data
@ApiModel(description = "关系类型更新Dto")
public class RelationTypeUpdateRequest implements Serializable {

    @ApiModelProperty("relationTypeId")
    Long id;

    @ApiModelProperty("实体之间的关系名称")
    String relationName;

    @ApiModelProperty("头节点Item的id")
    Long startItemId;

    @ApiModelProperty("尾节点ItemId")
    Long endItemId;


}
