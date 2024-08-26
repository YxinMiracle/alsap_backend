package com.yxinmiracle.alsap.controller;

import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.model.enums.ItemTypeEnum;
import com.yxinmiracle.alsap.model.vo.home.HomeViewDataVo;
import com.yxinmiracle.alsap.model.vo.home.ItemHomeVo;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页信息展示controller
 */
@RestController
@RequestMapping("/home")
@Slf4j
public class HomeController {

    @Resource
    private ItemService itemService;

    @Resource
    private CtiService ctiService;

    // region 增删改查
    @GetMapping("/home/view/sdoItemsCount")
    public BaseResponse<HomeViewDataVo> getHomeSdoItemsCount() {
        HomeViewDataVo homeViewDataVo = new HomeViewDataVo();
        List<ItemHomeVo> sdoItemHomeVos = itemService.getHomeViewDiffListByItemType(ItemTypeEnum.SDO.getValue());
        homeViewDataVo.setSdoItemHomeVos(sdoItemHomeVos);
        homeViewDataVo.setSdoCount(itemService.getTotalItemTypeCountInDbByItemType(ItemTypeEnum.SDO.getValue()));
        return ResultUtils.success(homeViewDataVo);
    }

    @GetMapping("/home/view/scoItemsCount")
    public BaseResponse<HomeViewDataVo> getHomeScoItemsCount() {
        HomeViewDataVo homeViewDataVo = new HomeViewDataVo();
        List<ItemHomeVo> scoItemHomeVos = itemService.getHomeViewDiffListByItemType(ItemTypeEnum.SCO.getValue());
        homeViewDataVo.setScoItemHomeVos(scoItemHomeVos);
        homeViewDataVo.setScoCount(itemService.getTotalItemTypeCountInDbByItemType(ItemTypeEnum.SCO.getValue()));
        return ResultUtils.success(homeViewDataVo);
    }

    @GetMapping("/home/view/ctiTotalCount")
    public BaseResponse<HomeViewDataVo> getHomeCtiTotalCount() {
        HomeViewDataVo homeViewDataVo = new HomeViewDataVo();
        homeViewDataVo.setCtiCount((int) ctiService.count());
        return ResultUtils.success(homeViewDataVo);
    }
}
