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
import com.yxinmiracle.alsap.model.dto.ttp.inner.TtpAddRequest;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.CtiTtps;
import com.yxinmiracle.alsap.model.enums.TtpStatusEnum;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.service.CtiTtpsService;
import com.yxinmiracle.alsap.utils.YxinMiracleObjectUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.builder.BuilderException;
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
     * 提供给ai服务进行请求，这个请求主要是接受ai服务处理好的情报数据
     *
     * @param ttpAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非法数据格式，请勿非法请求接口");
        }

        // 查看这个ctiId是否存在于数据库中
        Long ctiId = ttpAddRequest.getCtiId();
        Cti cti = ctiService.getById(ctiId);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cti), ErrorCode.PARAMS_ERROR, "非法CTI_ID，请勿非法请求接口");

        // 校验完成，可以进行数据添加
        CtiTtps ctiTtpsInDb = new CtiTtps();
        try {
            ctiTtpsInDb = ctiTtpsService.getOne(new LambdaQueryWrapper<CtiTtps>().eq(CtiTtps::getCtiId, ctiId), true);
            ctiTtpsInDb.setStatus(TtpStatusEnum.COMPLETED.getValue());
            ctiTtpsInDb.setSentLevelTtp(ttpAddRequest.getSentLevelTtp());
            ctiTtpsInDb.setArticleLevelTtp(ttpAddRequest.getArticleLevelTtp());
        }catch (Exception e){
            log.error("数据库getOne出现错误，需要进行调整，可能出现了冗余数据");
            throw new BusinessException(ErrorCode.DB_DATA_ERROR);
        }

        boolean save = ctiTtpsService.updateById(ctiTtpsInDb);
        ThrowUtils.throwIf(!save, ErrorCode.PARAMS_ERROR);

        return ResultUtils.success(true);
    }


}
