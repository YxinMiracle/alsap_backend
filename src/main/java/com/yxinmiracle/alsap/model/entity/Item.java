package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体信息表
 *
 * @TableName item
 */
@TableName(value = "item")
@Data
public class Item implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体的名称
     */
    private String itemName;

    /**
     * 实体的解释
     */
    private String itemContent;

    /**
     * 实体标注时前端展示的背景颜色
     */
    private String backgroundColor;

    /**
     * 实体标注时前端展示的字体颜色
     */
    private String textColor;

    /**
     * 实体类型，2是sdo,1是sco
     */
    private Integer itemType;

    /**
     * 对itemType的一个解释
     */
    private String itemTypeContent;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", itemName=").append(itemName);
        sb.append(", itemContent=").append(itemContent);
        sb.append(", backgroundColor=").append(backgroundColor);
        sb.append(", textColor=").append(textColor);
        sb.append(", itemType=").append(itemType);
        sb.append(", itemTypeContent=").append(itemTypeContent);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }


}