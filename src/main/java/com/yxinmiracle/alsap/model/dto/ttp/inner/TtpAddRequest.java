package com.yxinmiracle.alsap.model.dto.ttp.inner;

/*
 * @author  YxinMiracle
 * @date  2024-09-17 10:20
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "添加ttp请求")
public class TtpAddRequest implements Serializable {

    private Long ctiId;

    /**
     * 句子级别的ttp识别结果，存储为json字符串
     */
    private String sentLevelTtp;

    /**
     * 去重过后的文章级别ttp识别结果，存储为json字符串
     */
    private String articleLevelTtp;

    private static final long serialVersionUID = 1L;
}
