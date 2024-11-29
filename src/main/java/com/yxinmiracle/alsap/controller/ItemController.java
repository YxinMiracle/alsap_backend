package com.yxinmiracle.alsap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxinmiracle.alsap.annotation.AuthCheck;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.annotation.IpFlowLimit;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.constant.UserConstant;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.item.ItemDeleteRequest;
import com.yxinmiracle.alsap.model.dto.item.ItemQueryRequest;
import com.yxinmiracle.alsap.model.dto.item.ItemUpdateRequest;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.item.ItemVo;
import com.yxinmiracle.alsap.service.ItemService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/item")
@Slf4j
public class ItemController {

    @Resource
    private ItemService itemService;


    // region 增删改查

    /**
     * 根据请求参数来查询数据
     *
     * @param itemQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "根据Item查询信息返回ItemVo信息")
    @DecryptRequestBody
    @IpFlowLimit(resourceName = "getItemByPage")
    public BaseResponse<Page<ItemVo>> getItemByPage(@RequestBody ItemQueryRequest itemQueryRequest, HttpServletRequest request) {
        // 获取当前页数以及对应的请求大小
        long current = itemQueryRequest.getCurrent();
        long size = itemQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 先进行分页查询，找到分页后的item信息
        Page<Item> itemPage = itemService.page(new Page<>(current, size), itemService.getQueryWrapper(itemQueryRequest));


        return ResultUtils.success(itemService.getItemVOPage(itemPage, request));
    }

    /**
     * 获取Item Map
     */
    @GetMapping("/all")
    @ApiOperation(value = "返回item的Map形数据, 这可以用来规定下拉框的选择")
    public BaseResponse<Map<Long, Item>> getAllItemMapData(HttpServletRequest request) {
        Map<Long, Item> itemId2ItemMap = itemService.getItemId2ItemMap();
        if (ObjectUtils.isEmpty(itemId2ItemMap)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(itemId2ItemMap);
    }

    /**
     * 更新item（仅管理员）
     * 前端利用组件级别的控制
     *
     * @param itemUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("item更新,只有管理员")
    @DecryptRequestBody
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateItem(@RequestBody ItemUpdateRequest itemUpdateRequest, HttpServletRequest request) {
        if (itemUpdateRequest == null || itemUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Item item = new Item();
        BeanUtils.copyProperties(itemUpdateRequest, item);
        // 参数校验
        itemService.validItem(item, false);
        long id = itemUpdateRequest.getId();
        // 判断是否存在
        Item oldItem = itemService.getById(id);
        ThrowUtils.throwIf(oldItem == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = itemService.updateById(item);

        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "根据ItemId删除对应的Item，需要管理员权限")
    @DecryptRequestBody
    @Transactional
    public BaseResponse<Long> deleteItemByItemId(@RequestBody ItemDeleteRequest itemDeleteRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(itemDeleteRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Item item = new Item();
        BeanUtils.copyProperties(itemDeleteRequest, item);
        boolean result = itemService.removeById(item);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(item.getId());
    }


}
