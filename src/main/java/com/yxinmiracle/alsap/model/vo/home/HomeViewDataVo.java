package com.yxinmiracle.alsap.model.vo.home;

/*
 * @author  YxinMiracle
 * @date  2024-05-15 16:50
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首页实体类型展示VO
 */
@Data
public class HomeViewDataVo implements Serializable {

    Integer ctiCount;

    Integer SdoCount;

    Integer ScoCount;

    List<ItemHomeVo> sdoItemHomeVos;

    List<ItemHomeVo> scoItemHomeVos;
}
