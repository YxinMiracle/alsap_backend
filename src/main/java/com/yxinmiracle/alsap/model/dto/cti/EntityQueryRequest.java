package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-10 16:04
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class EntityQueryRequest implements Serializable {


    private String entityName; // 获得搜索框输入的文字
    private Integer itemId;  // 获得搜索框对应的id 要是用户没有选择那么就是0

    private static final long serialVersionUID = 1L;


}
