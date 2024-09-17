package com.yxinmiracle.alsap.model.vo.user;

/*
 * @author  YxinMiracle
 * @date  2024-09-11 15:12
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "没有包含用户角色的Vo")
public class NoRoleUserVo {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
