package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-11-15 20:29
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class CtiEntityInnerAddRequest {


    private Long ctiId;
    private String wordList;
    private String labelList;


}
