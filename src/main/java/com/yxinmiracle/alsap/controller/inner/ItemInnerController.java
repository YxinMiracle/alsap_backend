package com.yxinmiracle.alsap.controller.inner;

/*
 * @author  YxinMiracle
 * @date  2024-09-17 9:50
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yxinmiracle.alsap.annotation.DecryptInnerRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 情报ttp接口
 */
@RestController
@RequestMapping("/inner/item")
@Slf4j
public class ItemInnerController {

    @Resource
    private ItemService itemService;

    /**
     * 根据item的名称，返回item的id
     *
     * @param itemName
     * @param request
     * @return
     */
    @PostMapping("/{itemName}")
    @DecryptInnerRequestBody
    public BaseResponse<Long> getItemIdByItemName(@PathVariable("itemName") String itemName, HttpServletRequest request) {
        if (StringUtils.isBlank(itemName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Item resItem = itemService.getOne(new QueryWrapper<Item>().eq("itemName", itemName));
        return ResultUtils.success(resItem.getId());
    }

}
