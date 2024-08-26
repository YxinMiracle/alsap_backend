package com.yxinmiracle.alsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.home.ItemHomeVo;

import java.util.List;

/**
 * @Entity generator.domain.Item
 */
public interface ItemMapper extends BaseMapper<Item> {

    public List<ItemHomeVo> getHomeViewDiffListByItemType(Integer itemType);

    public Integer getTotalItemTypeCountInDbByItemType(Integer itemType);

}




