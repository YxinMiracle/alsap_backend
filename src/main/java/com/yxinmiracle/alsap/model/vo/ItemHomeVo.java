package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2024-05-12 11:39
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

@Data
public class ItemHomeVo implements Serializable {
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
     * 实体类型，2是sdo,1是sco
     */
    private short itemType;

    private Integer itemDbCount;

    private Integer totalItemDbCount;

    /**
     * 对itemType的一个解释
     */
    private String itemTypeContent;

    /**
     * 实体标注时前端展示的背景颜色
     */
    private String backgroundColor;

    /**
     * 实体标注时前端展示的字体颜色
     */
    private String textColor;

}
