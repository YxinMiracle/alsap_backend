package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-17 19:54
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EntityJumpNumQuery implements Serializable {
    String entityName;
    Integer jumpNum;
}
