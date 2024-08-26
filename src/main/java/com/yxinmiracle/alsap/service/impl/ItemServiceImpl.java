package com.yxinmiracle.alsap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.mapper.ItemMapper;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.home.ItemHomeVo;
import com.yxinmiracle.alsap.service.ItemService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 有关于item的炒作
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

    @Override
    public Integer getTotalItemTypeCountInDbByItemType(Integer itemType) {
        if (ObjectUtils.isEmpty(itemType)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return itemMapper.getTotalItemTypeCountInDbByItemType(itemType);
    }

    @Override
    public Map<Long, Item> getItemId2ItemMap() {
        List<Item> items = itemMapper.selectList(null);
        return items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

    }

}




