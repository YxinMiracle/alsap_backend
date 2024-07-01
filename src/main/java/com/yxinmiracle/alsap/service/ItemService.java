package com.yxinmiracle.alsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxinmiracle.alsap.model.entity.Post;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.ItemHomeVo;

import java.util.List;

/**
 *
 */
public interface ItemService extends IService<Item> {
    public List<com.yxinmiracle.alsap.model.entity.Item> getItems();

    List<ItemHomeVo> getHomeViewDiffListByItemType(Integer itemType);
}
