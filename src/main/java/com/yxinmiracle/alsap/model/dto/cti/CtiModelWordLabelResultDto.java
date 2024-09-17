package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-09-03 20:28
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用于分页请求Cti文章模型抽取后的实体信息")
public class CtiModelWordLabelResultDto implements Serializable {

    Long ctiId;
    Integer current;
    Integer size;
}
