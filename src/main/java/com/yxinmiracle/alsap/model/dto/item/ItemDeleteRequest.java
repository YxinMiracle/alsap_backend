package com.yxinmiracle.alsap.model.dto.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/*
 * @author  YxinMiracle
 * @date  2024-05-15 9:10
 * @Gitee: https://gitee.com/yxinmiracle
 */

@Data
@ApiModel(value = "item删除请求类")
public class ItemDeleteRequest implements Serializable {

    @ApiModelProperty(value = "item删除的id")
    private Long id;

}
