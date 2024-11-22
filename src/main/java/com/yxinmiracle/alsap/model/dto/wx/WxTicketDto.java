package com.yxinmiracle.alsap.model.dto.wx;

/*
 * @author  YxinMiracle
 * @date  2024-11-19 15:37
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class WxTicketDto {

    private Integer errcode;
    private String errmsg;
    private String ticket;
    private String expires_in;

}
