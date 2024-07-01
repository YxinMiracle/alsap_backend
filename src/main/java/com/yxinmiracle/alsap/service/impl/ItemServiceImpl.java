package com.yxinmiracle.alsap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.mapper.ItemMapper;
import com.yxinmiracle.alsap.model.vo.ItemHomeVo;
import com.yxinmiracle.alsap.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item>
    implements ItemService{

    @Resource
    private ItemMapper itemMapper;

    public List<Item> getItems(){
        List<Item> items = itemMapper.selectList(null);
        return items;
    }

    @Override
    public List<ItemHomeVo> getHomeViewDiffListByItemType(Integer itemType) {
        return itemMapper.getHomeViewDiffListByItemType(itemType);
    }

}




