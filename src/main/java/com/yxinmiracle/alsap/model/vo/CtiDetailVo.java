package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2024-05-10 16:34
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.entity.CtiChunk;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CtiDetailVo {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    private String content;

    private Integer entityNum;

    private Integer SdoNum;  // 域对象数量

    private Integer ScoNum; // 可观测对象数量

    private List<CtiChunk> ctiChunkList;

    private List<List<String>> wordList;

    private List<List<String>> labelList;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
