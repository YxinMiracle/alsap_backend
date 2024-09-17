package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-08-31 16:44
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "cti情报详情数据dto")
@Data
public class CtiDetailQueryRequest implements Serializable {

    @ApiModelProperty("cti情报的id")
    Long id;
}
