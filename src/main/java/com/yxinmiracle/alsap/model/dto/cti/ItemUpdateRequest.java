package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-15 20:02
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class ItemUpdateRequest implements Serializable {

    private Long id;


    /**
     * 实体标注时前端展示的背景颜色
     */
    private String backgroundColor;

}
