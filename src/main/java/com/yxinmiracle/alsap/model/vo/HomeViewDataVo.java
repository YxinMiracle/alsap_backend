package com.yxinmiracle.alsap.model.vo;

/*
 * @author  YxinMiracle
 * @date  2024-05-15 16:50
 * @Gitee: https://gitee.com/yxinmiracle
 */

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HomeViewDataVo implements Serializable {

    Integer ctiCount;

    Integer SdoCount;

    Integer ScoCount;

    List<ItemHomeVo> sdoItemHomeVos;

    List<ItemHomeVo> scoItemHomeVos;
}
