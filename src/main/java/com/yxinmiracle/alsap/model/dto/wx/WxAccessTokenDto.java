package com.yxinmiracle.alsap.model.dto.wx;

/*
 * @author  YxinMiracle
 * @date  2024-11-19 14:46
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class WxAccessTokenDto {
    private String access_token;
    private String expires_in;
}
