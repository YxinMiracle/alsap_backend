package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName cti_ttps
 */
@TableName(value ="cti_ttps")
@Data
public class CtiTtps implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 
     */
    private Long ctiId;

    /**
     * 句子级别的ttp识别结果，存储为json字符串
     */
    private String sentLevelTtp;

    /**
     * 去重过后的文章级别ttp识别结果，存储为json字符串
     */
    private String articleLevelTtp;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}