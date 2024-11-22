package com.yxinmiracle.alsap.controller.inner;

/*
 * @author  YxinMiracle
 * @date  2024-09-17 9:50
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.annotation.DecryptInnerRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.CtiChunkAddRequest;
import com.yxinmiracle.alsap.model.dto.cti.CtiChunkDto;
import com.yxinmiracle.alsap.model.dto.cti.CtiEntityInnerAddRequest;
import com.yxinmiracle.alsap.model.dto.cti.CtiUpdateRequest;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.CtiChunk;
import com.yxinmiracle.alsap.service.CtiChunkService;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.utils.YxinMiracleObjectUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 情报ttp接口
 */
@RestController
@RequestMapping("/inner/cti")
@Slf4j
public class CtiInnerController {

    @Resource
    private CtiService ctiService;

    @Resource
    private CtiChunkService ctiChunkService;

    /**
     * 提供给ai服务进行请求，用来接收定时爬取到的cti添加实体的信息
     *
     * @param ctiEntityInnerAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add/entity")
    @ApiOperation(value = "内部请求，用于添加cti实体")
    @Transactional
    @DecryptInnerRequestBody
    public BaseResponse<Boolean> addCtiEntity(@RequestBody CtiEntityInnerAddRequest ctiEntityInnerAddRequest, HttpServletRequest request) {
        // 接口校验
        if (YxinMiracleObjectUtils.areAllFieldsNull(ctiEntityInnerAddRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti ctiInDb = ctiService.getById(ctiEntityInnerAddRequest.getCtiId());
        ctiInDb.setLabelList(ctiEntityInnerAddRequest.getLabelList());
        ctiInDb.setWordList(ctiEntityInnerAddRequest.getWordList());
        boolean save = ctiService.updateById(ctiInDb);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 添加前端可展示的chunk类型数据，是cti实体数据
     *
     * @param ctiChunkAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add/chunk")
    @ApiOperation(value = "内部请求，用于添加ctiChunk")
    @Transactional
    @DecryptInnerRequestBody
    public BaseResponse<Boolean> addCtiChunk(@RequestBody CtiChunkAddRequest ctiChunkAddRequest, HttpServletRequest request) {
        if (ctiChunkAddRequest == null || ctiChunkAddRequest.getCtiChunkData().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<CtiChunk> ctiChunkList = new ArrayList<>();
        for (CtiChunkDto ctiChunkDatum : ctiChunkAddRequest.getCtiChunkData()) {
            CtiChunk ctiChunk = new CtiChunk();
            BeanUtils.copyProperties(ctiChunkDatum, ctiChunk);
            ctiChunkList.add(ctiChunk);
        }
        boolean result = ctiChunkService.saveBatch(ctiChunkList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/update/content")
    @ApiOperation(value = "内部请求，用于根据id改变内容")
    @Transactional
    @DecryptInnerRequestBody
    public BaseResponse<Long> updateCtiContentByCtiId(@RequestBody CtiUpdateRequest ctiUpdateRequest, HttpServletRequest request) {
        if (ctiUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti ctiInDb = ctiService.getById(ctiUpdateRequest.getId());
        ctiInDb.setContent(ctiUpdateRequest.getContent());
        boolean result = ctiService.updateById(ctiInDb);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(ctiInDb.getId());
    }

}
