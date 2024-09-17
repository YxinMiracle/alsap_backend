package com.yxinmiracle.alsap.model.vo.graph;

import lombok.Data;

import java.io.Serializable;

/*
 * @author  YxinMiracle
 * @date  2024-09-05 14:34
 * @Gitee: https://gitee.com/yxinmiracle
 */

@Data
public class GraphVo implements Serializable {
    private GraphNodeVo startNode;
    private GraphNodeVo endNode;
    private RelationVo relation;
}
