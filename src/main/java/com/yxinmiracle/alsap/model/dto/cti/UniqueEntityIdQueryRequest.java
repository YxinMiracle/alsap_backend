package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-11-17 10:15
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class UniqueEntityIdQueryRequest implements Serializable {

    private Long ctiId;

    private String entityName;

    private Long itemId;

}
