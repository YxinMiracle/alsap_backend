package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * stix中规定的关系类型，我们规定好，不同类型的实体时间可以存在什么样的关系
 *
 * @TableName relation_type
 */
@TableName(value = "relation_type")
@Data
public class RelationType implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 头实体的在item表中的id
     */
    private Long startEntityItemId;

    /**
     * 尾实体的在item表中的id
     */
    private Long endEntityItemId;

    /**
     * 关系的名称
     */
    private String relationName;

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
        RelationType other = (RelationType) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getStartEntityItemId() == null ? other.getStartEntityItemId() == null : this.getStartEntityItemId().equals(other.getStartEntityItemId()))
                && (this.getEndEntityItemId() == null ? other.getEndEntityItemId() == null : this.getEndEntityItemId().equals(other.getEndEntityItemId()))
                && (this.getRelationName() == null ? other.getRelationName() == null : this.getRelationName().equals(other.getRelationName()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStartEntityItemId() == null) ? 0 : getStartEntityItemId().hashCode());
        result = prime * result + ((getEndEntityItemId() == null) ? 0 : getEndEntityItemId().hashCode());
        result = prime * result + ((getRelationName() == null) ? 0 : getRelationName().hashCode());
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
        sb.append(", startEntityItemId=").append(startEntityItemId);
        sb.append(", endEntityItemId=").append(endEntityItemId);
        sb.append(", relationName=").append(relationName);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}