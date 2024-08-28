package com.yxinmiracle.alsap.model.vo.home;

/*
 * @author  YxinMiracle
 * @date  2024-05-12 11:39
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 首页展示Item信息的ItemVo
 */
@Data
public class ItemHomeVo implements Serializable {
    private Long id;
    /**
     * 实体类型的名称
     */
    private String itemName;

    /**
     * 实体的解释（暂时无用）
     */
    private String itemContent;

    /**
     * 实体类型，2是sdo,1是sco
     */
    private Integer itemType;

    /**
     * 整个实体的类型名称itemName在整个item表中占多少个
     */
    private Integer itemDbCount;



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
