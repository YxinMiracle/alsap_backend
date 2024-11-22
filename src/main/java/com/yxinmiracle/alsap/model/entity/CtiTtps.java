package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName cti_ttps
 */
@TableName(value = "cti_ttps")
@Data
public class CtiTtps implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * ctiId
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

    /**
     * 1. 0表示默认，没有开始，也没有失败
     * 2. 1表示已经调用了ai服务，但是还没有后端服务还有没有返回数据
     * 3. 2表示后端服务已经成功返回数据了，任务完成
     * 3. 3表示已经调用了ai服务，但是ai服务并没有正确返回对应的数据，现在需要调用定时服务去重新执行。
     */
    private Integer status;

    /**
     * 存储路径
     */
    private String savaPath;

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