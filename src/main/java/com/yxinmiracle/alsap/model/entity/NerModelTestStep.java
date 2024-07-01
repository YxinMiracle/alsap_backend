package com.yxinmiracle.alsap.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName ner_model_test_step
 */
@TableName(value ="ner_model_test_step")
@Data
public class NerModelTestStep implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer nerModelTestStepId;

    /**
     * 
     */
    private Integer stepNum;

    /**
     * 
     */
    private String nerModelTestContent;

    /**
     * 
     */
    private Integer nerModelTestTotalWordNum;

    /**
     * 
     */
    private Integer nerModelTestSentNum;

    /**
     * 
     */
    private Date nerModelTestCreateTime;

    /**
     * 是否已经重新训练
     */
    private short nerModelTestIsTest;

    /**
     * 是否需要重新训练
     */
    private short nerModelTestIsRetrain;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Alias("IsTestType")
    public enum IsTestTypeEnum{
        IsTest((short)1,"已经训练过"),NotTest((short)0,"还没有训练过"),DontTest((short)-1, "不需要进行重新训练");
        @Getter
        short code;
        @Getter
        String msg;
        IsTestTypeEnum(short code,String msg){
            this.code = code;
            this.msg = msg;
        }
    }

    @Alias("IsRetrainType")
    public enum IsRetrainTypeEnum{
        IsRetrain((short)1,"需要重新训练"),NotRetrain((short)0,"不需要重新训练");
        @Getter
        short code;
        @Getter
        String msg;
        IsRetrainTypeEnum(short code,String msg){
            this.code = code;
            this.msg = msg;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", nerModelTestStepId=").append(nerModelTestStepId);
        sb.append(", stepNum=").append(stepNum);
        sb.append(", nerModelTestContent=").append(nerModelTestContent);
        sb.append(", nerModelTestTotalWordNum=").append(nerModelTestTotalWordNum);
        sb.append(", nerModelTestSentNum=").append(nerModelTestSentNum);
        sb.append(", nerModelTestCreateTime=").append(nerModelTestCreateTime);
        sb.append(", nerModelTestIsTest=").append(nerModelTestIsTest);
        sb.append(", nerModelTestIsRetrain=").append(nerModelTestIsRetrain);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}