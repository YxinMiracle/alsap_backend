package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-16 20:29
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PreventEntityQuery implements Serializable {

    List<Long> startNodeIdList;
    List<Long> itemTypeIds;

}
