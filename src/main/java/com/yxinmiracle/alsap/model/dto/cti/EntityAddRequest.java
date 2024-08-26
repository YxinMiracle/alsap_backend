package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-12 9:24
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class EntityAddRequest implements Serializable {


    /**
     * 实体内容，内容相同的我就不需要去插入了
     */
    private String entityName;

    /**
     * 这个实体对应的CTI文章是什么
     */
    private Long ctiId;

    private Long itemId;

}
