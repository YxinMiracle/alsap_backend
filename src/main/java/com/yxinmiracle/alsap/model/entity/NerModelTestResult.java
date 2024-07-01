package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName ner_model_test_result
 */
@TableName(value ="ner_model_test_result")
@Data
public class NerModelTestResult implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long nerModelTestResultId;

    /**
     * 
     */
    private Integer nerModelTestStepId;

    /**
     * 
     */
    private String nerModelTestResultWord;

    /**
     * 
     */
    private String nerModelTestResultTrueLabel;

    /**
     * 
     */
    private String nerModelTestResultPredLabel;

    /**
     * 
     */
    private short nerModelTestResultIsTrue;

    /**
     * 
     */
    private Integer nerModelTestResultWordIndex;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", nerModelTestResultId=").append(nerModelTestResultId);
        sb.append(", nerModelTestStepId=").append(nerModelTestStepId);
        sb.append(", nerModelTestResultWord=").append(nerModelTestResultWord);
        sb.append(", nerModelTestResultTrueLabel=").append(nerModelTestResultTrueLabel);
        sb.append(", nerModelTestResultPredLabel=").append(nerModelTestResultPredLabel);
        sb.append(", nerModelTestResultIsTrue=").append(nerModelTestResultIsTrue);
        sb.append(", nerModelTestResultWordIndex=").append(nerModelTestResultWordIndex);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}