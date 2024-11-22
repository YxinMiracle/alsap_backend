package com.yxinmiracle.alsap.model.vo.graph;

/*
 * @author  YxinMiracle
 * @date  2024-11-18 11:47
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import lombok.Data;

import java.util.List;

@Data
public class NodeRelCtiVo {

    private List<CtiVo> relatedCti;

}
