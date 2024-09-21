package com.yxinmiracle.alsap.model.vo.cti;

/*
 * @author  YxinMiracle
 * @date  2024-09-20 11:04
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CtiTtpExtractVo {

    Long ctiId;
    String content;

}
