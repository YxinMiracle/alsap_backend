package com.yxinmiracle.alsap.controller.inner;

/*
 * @author  YxinMiracle
 * @date  2024-09-17 9:50
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxinmiracle.alsap.annotation.DecryptInnerRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.EntityAddRequest;
import com.yxinmiracle.alsap.model.dto.cti.UniqueEntityIdQueryRequest;
import com.yxinmiracle.alsap.model.entity.Entity;
import com.yxinmiracle.alsap.service.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 情报ttp接口
 */
@RestController
@RequestMapping("/inner/entity")
@Slf4j
public class EntityInnerController {

    @Resource
    private EntityService entityService;

    @PostMapping("/unique/add")
    @DecryptInnerRequestBody
    public BaseResponse<Long> addUniqueEntity(@RequestBody EntityAddRequest entityAddRequest) {
        if (ObjectUtils.isEmpty(entityAddRequest)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Entity entity = new Entity();
        BeanUtils.copyProperties(entityAddRequest, entity);
        Entity dbEntity = entityService.getOne(new LambdaQueryWrapper<Entity>()
                .eq(Entity::getCtiId, entity.getCtiId())
                .eq(Entity::getEntityName, entity.getEntityName())
                .eq(Entity::getItemId, entity.getItemId()));
        // 我需要判断这个实体在不在数据库了，在数据库我就返回这个在数据库里面的实体id，并不做插入
        if (ObjectUtils.isNotEmpty(dbEntity)) {
            // 表示这个实体并不存在数据库中
            return ResultUtils.success(dbEntity.getId());
        }
        // 如果没有走上面，那么就表示需要进行实体的插入
        boolean result = entityService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(entity.getId());
    }

    /**
     * 这里是为了通过ctiId和对应的entityName去获得到对应的实体id，在关系添加的时候需要用到
     *
     * @param uniqueEntityIdQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/unique/entity_id")
    @DecryptInnerRequestBody
    public BaseResponse<Long> getUniqueEntityIdByCtiIdAndEntityName(@RequestBody UniqueEntityIdQueryRequest uniqueEntityIdQueryRequest,
                                                                    HttpServletRequest request) {
        Long ctiId = uniqueEntityIdQueryRequest.getCtiId();
        String entityName = uniqueEntityIdQueryRequest.getEntityName();
        Long itemId = uniqueEntityIdQueryRequest.getItemId();

        if (ObjectUtils.isEmpty(ctiId) || StringUtils.isBlank(entityName)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Entity entity = entityService.getOne(new LambdaQueryWrapper<Entity>()
                .eq(Entity::getCtiId, ctiId)
                .eq(Entity::getEntityName, entityName)
                .eq(Entity::getItemId, itemId));
        ThrowUtils.throwIf(!ObjectUtils.isNotEmpty(entity), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(entity.getId());
    }

}
