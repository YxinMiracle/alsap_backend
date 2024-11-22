package com.yxinmiracle.alsap.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yxinmiracle.alsap.model.dto.item.ItemQueryRequest;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.home.ItemHomeVo;
import com.yxinmiracle.alsap.model.vo.item.ItemVo;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface ItemService extends IService<Item> {
    public List<com.yxinmiracle.alsap.model.entity.Item> getItems();

    /**
     * 根据ItemType，也就是属于SCO还是属于SDO类型的实体，然后根据这itemType去获取信息
     *
     * @param itemType
     * @return
     */
    @ApiOperation(value = "返回一个list，list里面的是每个item在整个cti_chunk表中的数值")
    List<ItemHomeVo> getHomeViewDiffListByItemType(Integer itemType);


    @ApiOperation(value = "根据itemType的数值（0或者1），在整个db中的数量")
    Integer getTotalItemTypeCountInDbByItemType(Integer itemType);

    @ApiOperation(value = "返回一个Map，key为item的id，value为item")
    Map<Long, Item> getItemId2ItemMap();

    LambdaQueryWrapper<Item> getQueryWrapper(ItemQueryRequest itemQueryRequest);

    Page<ItemVo> getItemVOPage(Page<Item> itemPage, HttpServletRequest request);

    void validItem(Item item, boolean add);
}
