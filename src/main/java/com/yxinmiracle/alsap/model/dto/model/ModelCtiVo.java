package com.yxinmiracle.alsap.model.dto.model;

/*
 * @author  YxinMiracle
 * @date  2024-05-14 17:19
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModelCtiVo implements Serializable {

    private Long id;

    private List<List<String>> wordList;

    private List<List<String>> labelList;

}
