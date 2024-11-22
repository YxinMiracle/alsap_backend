package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 情报规则表
 *
 * @TableName cti_rules
 */
@TableName(value = "cti_rules")
@Data
public class CtiRules implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联的情报Id
     */
    private Long ctiId;

    /**
     * 存放大模型生成的结果
     */
    private String llmResult;

    /**
     * 1表示还没有请求llm，2表表示已经请求了llm，3已经获得到llm的响应
     */
    private Integer llmStatus;

    /**
     * 1表示还没有生成规则文件、2表示已经生成了规则文件
     */
    private Integer ruleFileStatus;

    /**
     * 1表示生成的是yara规则，2表示生成的是snort规则
     */
    private Integer ruleType;

    /**
     *
     */
    private String filePath;

    /**
     *
     */
    private String ruleName;

    /**
     *
     */
    private String ruleDescription;


    /**
     *
     */
    private String singleRequestId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 是否删除
     */
    private Byte isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}