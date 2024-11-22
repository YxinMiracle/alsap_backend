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
import com.yxinmiracle.alsap.model.entity.RelationType;
import com.yxinmiracle.alsap.service.RelationTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
@RequestMapping("/inner/relation/type")
@Slf4j
public class RelationTypeInnerController {

    @Resource
    private RelationTypeService relationTypeService;

    @PostMapping("/is_relation/{startItemId}/{endItemId}/{relationName}")
    @DecryptInnerRequestBody
    public BaseResponse<Long> getItemIdByItemName(@PathVariable("startItemId") Long startItemId,
                                                  @PathVariable("endItemId") Long endItemId,
                                                  @PathVariable("relationName") String relationName,
                                                  HttpServletRequest request) {
        if (ObjectUtils.isEmpty(startItemId) || ObjectUtils.isEmpty(endItemId) || StringUtils.isBlank(relationName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelationType relationType = relationTypeService.getOne(new LambdaQueryWrapper<RelationType>()
                .eq(RelationType::getStartEntityItemId, startItemId)
                .eq(RelationType::getEndEntityItemId, endItemId)
                .eq(RelationType::getRelationName, relationName));
        return ResultUtils.success(relationType.getId());
    }
}
