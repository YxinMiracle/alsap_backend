package com.yxinmiracle.alsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.mapper.ItemMapper;
import com.yxinmiracle.alsap.model.dto.item.ItemQueryRequest;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.home.ItemHomeVo;
import com.yxinmiracle.alsap.model.vo.item.ItemVo;
import com.yxinmiracle.alsap.service.ItemService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 有关于item的炒作
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item>
        implements ItemService {

    @Resource
    private ItemMapper itemMapper;

    public List<Item> getItems() {
        List<Item> items = itemMapper.selectList(null);
        return items;
    }

    @Override
    public List<ItemHomeVo> getHomeViewDiffListByItemType(Integer itemType) {
        return itemMapper.getHomeViewDiffListByItemType(itemType);
    }

    @Override
    public Integer getTotalItemTypeCountInDbByItemType(Integer itemType) {
        if (ObjectUtils.isEmpty(itemType)) {
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

    @Override
    public LambdaQueryWrapper<Item> getQueryWrapper(ItemQueryRequest itemQueryRequest) {
        LambdaQueryWrapper<Item> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtils.isEmpty(itemQueryRequest)) {
            return queryWrapper;
        }

        String itemName = itemQueryRequest.getItemName();
        String itemContent = itemQueryRequest.getItemContent();
        Integer itemType = itemQueryRequest.getItemType();
        String itemTypeContent = itemQueryRequest.getItemTypeContent();
        Date createTime = itemQueryRequest.getCreateTime();
        Date updateTime = itemQueryRequest.getUpdateTime();

        queryWrapper.like(StringUtils.isNotBlank(itemName), Item::getItemName, itemName);
        queryWrapper.like(StringUtils.isNotBlank(itemContent), Item::getItemContent, itemContent);
        queryWrapper.eq(ObjectUtils.isNotEmpty(itemType), Item::getItemType, itemType);
        queryWrapper.like(StringUtils.isNotBlank(itemTypeContent), Item::getItemTypeContent, itemTypeContent);
        queryWrapper.eq(ObjectUtils.isNotEmpty(createTime), Item::getCreateTime, createTime);
        queryWrapper.eq(ObjectUtils.isNotEmpty(updateTime), Item::getUpdateTime, updateTime);

        return queryWrapper;
    }

    @Override
    public Page<ItemVo> getItemVOPage(Page<Item> itemPage, HttpServletRequest request) {
        List<Item> itemList = itemPage.getRecords();
        Page<ItemVo> itemVoPage = new Page<>(itemPage.getPages(), itemPage.getSize(), itemPage.getTotal());

        if (ObjectUtils.isEmpty(itemList)) {
            return itemVoPage;
        }

        List<ItemVo> itemVoList = itemList.stream().map(item -> ItemVo.objToVo(item)).collect(Collectors.toList());
        itemVoPage.setRecords(itemVoList);
        return itemVoPage;
    }

    @Override
    public void validItem(Item item, boolean add) {
        if (item == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String itemName = item.getItemName();
        String itemContent = item.getItemContent();
        String backgroundColor = item.getBackgroundColor();
        String textColor = item.getTextColor();
        Integer itemType = item.getItemType();
        String itemTypeContent = item.getItemTypeContent();
        // 创建时，参数不能为空
        ThrowUtils.throwIf(ObjectUtils.isEmpty(itemType), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isAnyBlank(itemName, itemContent, backgroundColor, textColor, itemTypeContent), ErrorCode.PARAMS_ERROR);
        // 有参数则校验
        if (StringUtils.isNotBlank(itemName) && itemName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "item名字过长");
        }
        if (StringUtils.isNotBlank(itemContent) && itemContent.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "item内容过长");
        }
        if (!backgroundColor.startsWith("#") || !textColor.startsWith("#") || backgroundColor.length() != 7 || textColor.length() != 7) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "背景颜色或者字体颜色设置不合法");
        }
    }

}




