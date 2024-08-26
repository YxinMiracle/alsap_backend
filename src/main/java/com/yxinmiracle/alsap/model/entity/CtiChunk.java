package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * cti提交表
 * @TableName cti_chunk
 */
@TableName(value ="cti_chunk")
@Data
public class CtiChunk implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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
     * 创建这个实体的用户ID
     */
    private Long userId;

    /**
     * 在文本中的开始位置
     */
    private Integer startOffset;

    /**
     * 在文本中的结束位置
     */
    private Integer endOffset;

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
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CtiChunk other = (CtiChunk) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCtiId() == null ? other.getCtiId() == null : this.getCtiId().equals(other.getCtiId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getSentText() == null ? other.getSentText() == null : this.getSentText().equals(other.getSentText()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getStartOffset() == null ? other.getStartOffset() == null : this.getStartOffset().equals(other.getStartOffset()))
            && (this.getEndOffset() == null ? other.getEndOffset() == null : this.getEndOffset().equals(other.getEndOffset()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCtiId() == null) ? 0 : getCtiId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getSentText() == null) ? 0 : getSentText().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getStartOffset() == null) ? 0 : getStartOffset().hashCode());
        result = prime * result + ((getEndOffset() == null) ? 0 : getEndOffset().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ctiId=").append(ctiId);
        sb.append(", itemId=").append(itemId);
        sb.append(", sentText=").append(sentText);
        sb.append(", userId=").append(userId);
        sb.append(", startOffset=").append(startOffset);
        sb.append(", endOffset=").append(endOffset);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}