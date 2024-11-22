package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-11-15 20:29
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class CtiInnerAddRequest {

    /**
     * 标题
     */
    private String title;

    /**
     * 无html内容
     */
    private String content;

    private String postUrl;

    private String htmlText;


}
