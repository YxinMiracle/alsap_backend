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
import com.yxinmiracle.alsap.model.dto.cti.RelationAddRequest;
import com.yxinmiracle.alsap.model.entity.Relation;
import com.yxinmiracle.alsap.service.RelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
@RequestMapping("/inner/relation")
@Slf4j
public class RelationInnerController {

    @Resource
    private RelationService relationService;

    @PostMapping("/add")
    @DecryptInnerRequestBody
    public BaseResponse<Long> addRelationData(@RequestBody RelationAddRequest relationAddRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(relationAddRequest)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Relation relation = new Relation();
        BeanUtils.copyProperties(relationAddRequest, relation);
        Relation dbRelation = relationService.getOne(new LambdaQueryWrapper<Relation>().eq(Relation::getCtiId, relation.getCtiId())
                .eq(Relation::getRelationTypeId, relation.getRelationTypeId())
                .eq(Relation::getStartCtiEntityId, relation.getStartCtiEntityId())
                .eq(Relation::getEndCtiEntityId, relation.getEndCtiEntityId()));
        if (!ObjectUtils.isEmpty(dbRelation)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = relationService.save(relation);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(relation.getId());
    }
}
