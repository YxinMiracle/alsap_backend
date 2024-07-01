package com.yxinmiracle.alsap.model.dto;

/*
 * @author  YxinMiracle
 * @date  2024-05-24 15:25
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;

@Data
public class RetrainRequest implements Serializable {
    private String trainData;
}
