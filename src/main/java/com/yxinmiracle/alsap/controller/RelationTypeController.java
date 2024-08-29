package com.yxinmiracle.alsap.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.model.dto.relationType.RelationTypeQueryRequest;
import com.yxinmiracle.alsap.model.vo.relationType.RelationTypeVo;
import com.yxinmiracle.alsap.service.RelationTypeService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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


}
