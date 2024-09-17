package com.yxinmiracle.alsap.model.vo.cti;

/*
 * @author  YxinMiracle
 * @date  2024-09-03 20:35
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CtiWordLabelVo implements Serializable {

    List<List<String>> wordList;
    List<List<String>> labelList;
    Integer total;

}
