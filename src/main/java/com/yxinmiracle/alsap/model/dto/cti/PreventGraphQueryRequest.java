package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-16 19:36
 * @Gitee: https://gitee.com/yxinmiracle
 */


import com.yxinmiracle.alsap.model.vo.CtiGraphVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PreventGraphQueryRequest implements Serializable {

    List<CtiGraphVo> graphData;
    List<Long> consideredItemId;

}
