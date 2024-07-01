package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName ner_model_test
 */
@TableName(value ="ner_model_test")
@Data
public class NerModelTest implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long nerModelTestId;

    /**
     * 
     */
    private String nerModelTestTypeName;

    /**
     * 
     */
    private String nerModelTestPrecision;

    /**
     * 
     */
    private String nerModelTestRecall;

    /**
     * 
     */
    private String nerModelTestF1Score;

    /**
     * 
     */
    private Integer nerModelTestStepId;

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
        NerModelTest other = (NerModelTest) that;
        return (this.getNerModelTestId() == null ? other.getNerModelTestId() == null : this.getNerModelTestId().equals(other.getNerModelTestId()))
            && (this.getNerModelTestTypeName() == null ? other.getNerModelTestTypeName() == null : this.getNerModelTestTypeName().equals(other.getNerModelTestTypeName()))
            && (this.getNerModelTestPrecision() == null ? other.getNerModelTestPrecision() == null : this.getNerModelTestPrecision().equals(other.getNerModelTestPrecision()))
            && (this.getNerModelTestRecall() == null ? other.getNerModelTestRecall() == null : this.getNerModelTestRecall().equals(other.getNerModelTestRecall()))
            && (this.getNerModelTestF1Score() == null ? other.getNerModelTestF1Score() == null : this.getNerModelTestF1Score().equals(other.getNerModelTestF1Score()))
            && (this.getNerModelTestStepId() == null ? other.getNerModelTestStepId() == null : this.getNerModelTestStepId().equals(other.getNerModelTestStepId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getNerModelTestId() == null) ? 0 : getNerModelTestId().hashCode());
        result = prime * result + ((getNerModelTestTypeName() == null) ? 0 : getNerModelTestTypeName().hashCode());
        result = prime * result + ((getNerModelTestPrecision() == null) ? 0 : getNerModelTestPrecision().hashCode());
        result = prime * result + ((getNerModelTestRecall() == null) ? 0 : getNerModelTestRecall().hashCode());
        result = prime * result + ((getNerModelTestF1Score() == null) ? 0 : getNerModelTestF1Score().hashCode());
        result = prime * result + ((getNerModelTestStepId() == null) ? 0 : getNerModelTestStepId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", nerModelTestId=").append(nerModelTestId);
        sb.append(", nerModelTestTypeName=").append(nerModelTestTypeName);
        sb.append(", nerModelTestPrecision=").append(nerModelTestPrecision);
        sb.append(", nerModelTestRecall=").append(nerModelTestRecall);
        sb.append(", nerModelTestF1Score=").append(nerModelTestF1Score);
        sb.append(", nerModelTestStepId=").append(nerModelTestStepId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}