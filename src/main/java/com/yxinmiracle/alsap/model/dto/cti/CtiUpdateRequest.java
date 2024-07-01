package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-09 20:29
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class CtiUpdateRequest {


    /**
     * 标题
     */
    private Long id;

    /**
     * 内容
     */
    private String content;


    private static final long serialVersionUID = 1L;
}
