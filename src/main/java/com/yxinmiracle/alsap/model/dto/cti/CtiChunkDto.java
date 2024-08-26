package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-09 21:47
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class CtiChunkDto {

    /**
     * cti情报表的ID
     */
    private Long ctiId;

    /**
     * 实体id，如果前端没有传，那么就是对应O的item
     */
    private Long itemId;

    /**
     * 属于这一块的文本信息（句子）
     */
    private String sentText;

    /**
     * 在文本中的开始位置
     */
    private Integer startOffset;

    /**
     * 在文本中的结束位置
     */
    private Integer endOffset;

    private static final long serialVersionUID = 1L;

}
