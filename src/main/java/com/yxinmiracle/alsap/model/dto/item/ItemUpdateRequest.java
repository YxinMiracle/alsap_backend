package com.yxinmiracle.alsap.model.dto.item;

/*
 * @author  YxinMiracle
 * @date  2024-05-09 20:29
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "Item更新请求类")
public class ItemUpdateRequest implements Serializable {


    private Long id;

    /**
     * 实体的名称
     */
    private String itemName;

    /**
     * 实体的解释
     */
    private String itemContent;

    /**
     * 实体标注时前端展示的背景颜色
     */
    private String backgroundColor;

    /**
     * 实体标注时前端展示的字体颜色
     */
    private String textColor;

    /**
     * 实体类型，2是sdo,1是sco
     */
    private Integer itemType;

    /**
     * 对itemType的一个解释
     */
    private String itemTypeContent;


    private static final long serialVersionUID = 1L;
}
