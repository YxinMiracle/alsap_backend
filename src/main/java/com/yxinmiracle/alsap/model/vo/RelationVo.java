package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2024-05-12 11:42
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.vo.item.ItemVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class RelationVo implements Serializable {

    ItemVo startFatherNode;

    ItemVo endFatherNode;
    /**
     * 关系的名称
     */
    private String relationName;
}
