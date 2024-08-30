package com.yxinmiracle.alsap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxinmiracle.alsap.annotation.AuthCheck;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.constant.UserConstant;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.relationType.RelationTypeQueryRequest;
import com.yxinmiracle.alsap.model.dto.relationType.RelationTypeUpdateRequest;
import com.yxinmiracle.alsap.model.entity.RelationType;
import com.yxinmiracle.alsap.model.vo.relationType.RelationTypeVo;
import com.yxinmiracle.alsap.service.RelationTypeService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/relationType")
@Slf4j
public class RelationTypeController {

    @Resource
    private RelationTypeService relationTypeService;


    // region 增删改查

    /**
     * 根据请求参数来查询数据
     *
     * @param relationTypeQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "根据Item查询信息返回ItemVo信息")
    @DecryptRequestBody
    public BaseResponse<Page<RelationTypeVo>> getRelationTypeByPage(@RequestBody RelationTypeQueryRequest relationTypeQueryRequest, HttpServletRequest request) {

        // 先进行分页查询，找到分页后的item信息
        Page<RelationTypeVo> relationTypeVoListPage = relationTypeService.getRelationTypeVoListPage(relationTypeQueryRequest);

        return ResultUtils.success(relationTypeVoListPage);
    }

    @GetMapping("/all")
    @ApiOperation(value = "获取relation_type列表,这里的relation_type是去重的")
    public BaseResponse<List<String>> getRelationTypeNameList() {
        List<String> relationTypeNameList = relationTypeService.getRelationTypeNameList();
        return ResultUtils.success(relationTypeNameList);
    }

    @PostMapping("/update")
    @ApiOperation(value = "relationType更新")
    @DecryptRequestBody
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateRelationType(@RequestBody RelationTypeUpdateRequest relationTypeUpdateRequest,
                                                    HttpServletRequest request) {
        if (relationTypeUpdateRequest == null || ObjectUtils.isEmpty(relationTypeUpdateRequest) || relationTypeUpdateRequest.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelationType relationType = new RelationType();
        relationType.setId(relationTypeUpdateRequest.getId());
        relationType.setStartEntityItemId(relationTypeUpdateRequest.getStartItemId());
        relationType.setEndEntityItemId(relationTypeUpdateRequest.getEndItemId());
        relationType.setRelationName(relationTypeUpdateRequest.getRelationName());

        relationTypeService.validRelationType(relationType);

        Long id = relationTypeUpdateRequest.getId();
        RelationType oldRelationType = relationTypeService.getById(id);
        ThrowUtils.throwIf(oldRelationType == null, ErrorCode.PARAMS_ERROR);
        boolean result = relationTypeService.updateById(relationType);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(result);
    }


}
