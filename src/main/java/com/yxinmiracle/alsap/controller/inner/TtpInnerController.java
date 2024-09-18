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
import com.yxinmiracle.alsap.model.dto.ttp.inner.TtpAddRequest;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.CtiTtps;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.service.CtiTtpsService;
import com.yxinmiracle.alsap.utils.YxinMiracleObjectUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/inner/ttp")
@Slf4j
public class TtpInnerController {

    @Resource
    private CtiService ctiService;

    @Resource
    private CtiTtpsService ctiTtpsService;

    /**
     * 根据请求参数来查询数据
     *
     * @param ttpAddRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "内部请求，用于添加ttp")
    @Transactional
    @DecryptInnerRequestBody
    public BaseResponse<Boolean> addCtiTtp(@RequestBody TtpAddRequest ttpAddRequest, HttpServletRequest request) {
        // 接口校验
        if (YxinMiracleObjectUtils.areAllFieldsNull(ttpAddRequest) || ObjectUtils.isEmpty(ttpAddRequest.getCtiId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 数据校验
        if (ttpAddRequest.getArticleLevelTtp().length() <= 10 || ttpAddRequest.getSentLevelTtp().length() <= 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查看这个ctiId是否存在于数据库中
        Long ctiId = ttpAddRequest.getCtiId();
        Cti cti = ctiService.getById(ctiId);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cti), ErrorCode.PARAMS_ERROR);

        // 校验完成，可以进行数据添加
        CtiTtps ctiTtps = new CtiTtps();
        BeanUtils.copyProperties(ttpAddRequest, ctiTtps);
        boolean save = ctiTtpsService.save(ctiTtps);
        ThrowUtils.throwIf(!save, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(true);
    }


}
