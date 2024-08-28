package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2024-05-12 11:32
 * @Gitee: https://gitee.com/yxinmiracle
 */


import com.yxinmiracle.alsap.model.vo.item.ItemVo;
import lombok.Data;

@Data
public class GraphNodeVo {

    private Long nodeId;
    /**
     * 实体内容，内容相同的我就不需要去插入了
     */
    private String entityName;

    /**
     * 这个实体对应的CTI文章是什么
     */
    private Long ctiId;

    private String ctiName;

    private ItemVo itemData;

}
