package com.yxinmiracle.alsap.model.dto.item;

/*
 * @author  YxinMiracle
 * @date  2024-05-10 16:04
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yxinmiracle.alsap.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Item查询请求Dto")
public class ItemQueryRequest extends PageRequest implements Serializable {

    /**
     * 实体的名称
     */
    @ApiModelProperty("item的名称")
    private String itemName;

    /**
     * 实体的解释
     */
    @ApiModelProperty("item的解释/内容")
    private String itemContent;

    /**
     * 实体类型，2是sdo,1是sco
     */
    @ApiModelProperty("item的类型,sco/sdo,2是sdo,1是sco")
    private Integer itemType;

    /**
     * 对itemType的一个解释
     */
    @ApiModelProperty("sco的全称或者是sdo的全称")
    private String itemTypeContent;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;


    private static final long serialVersionUID = 1L;


}
