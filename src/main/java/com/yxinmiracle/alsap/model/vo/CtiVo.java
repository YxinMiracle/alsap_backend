package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2024-05-09 22:43
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yxinmiracle.alsap.model.entity.CtiChunk;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CtiVo {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    private Integer entityNum;

    private Integer SdoNum;  // 域对象数量

    private Integer ScoNum; // 可观测对象数量

    private Integer hasGraph; // 可观测对象数量

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
