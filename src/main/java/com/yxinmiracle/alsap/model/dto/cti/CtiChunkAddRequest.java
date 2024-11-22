package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-09 20:29
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CtiChunkAddRequest implements Serializable {

    List<CtiChunkDto> ctiChunkData;

    private static final long serialVersionUID = 1L;
}
