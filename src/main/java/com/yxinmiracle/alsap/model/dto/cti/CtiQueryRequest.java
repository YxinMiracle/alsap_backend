package com.yxinmiracle.alsap.model.dto.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-10 16:04
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Cti查询请求Dto")
public class CtiQueryRequest extends PageRequest implements Serializable {

    /**
     * 标题
     */
    @ApiModelProperty(value = "Cti标题")
    private String title;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;


}
