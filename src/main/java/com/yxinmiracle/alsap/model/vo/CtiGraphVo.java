package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2024-05-12 11:27
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.vo.graph.GraphNodeVo;
import com.yxinmiracle.alsap.model.vo.graph.RelationVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class CtiGraphVo implements Serializable {

    private GraphNodeVo startNode;
    private GraphNodeVo endNode;
    private RelationVo relation;

}
