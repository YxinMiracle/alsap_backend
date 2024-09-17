package com.yxinmiracle.alsap.model.vo.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-10 16:34
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.entity.CtiChunk;
import com.yxinmiracle.alsap.model.vo.user.NoRoleUserVo;
import com.yxinmiracle.alsap.model.vo.user.UserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "用于情报详情界面的信息展示")
public class CtiDetailVo {
    /**
     * id
     */
    @ApiModelProperty("cti情报的id")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty("cti情报的标题")
    private String title;

    @ApiModelProperty("cti情报的内容")
    private String content;

    @ApiModelProperty("实体的总数量")
    private Integer entityNum;

    @ApiModelProperty("实体SDO数量")
    private Integer SdoNum;  // 域对象数量

    @ApiModelProperty("实体sco数量")
    private Integer ScoNum; // 可观测对象数量

    @ApiModelProperty("cti数据分块")
    private List<CtiChunk> ctiChunkList;

    @ApiModelProperty("创建该情报的User")
    private NoRoleUserVo user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
