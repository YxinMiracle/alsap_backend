package com.yxinmiracle.alsap.model.vo.cti;

/*
 * @author  YxinMiracle
 * @date  2024-05-09 22:43
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.util.Date;

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

    private Integer hasRule;

    private String postUrl;

    private Integer ctiCharCount; // 存储文章字符数量，用于前端计算对应的阅读时间

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
