package com.yxinmiracle.alsap.controller;

/*
 * @author  YxinMiracle
 * @date  2024-09-17 9:41
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.CtiQueryRequest;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 情报ttp接口
 */
@RestController
@RequestMapping("/list")
@Slf4j
public class TtpController {

    /**
     * 根据请求参数来查询数据
     *
     * @param ctiQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list")
    @ApiOperation(value = "根据Cti查询信息返回CtiVo信息")
    @DecryptRequestBody
    public BaseResponse<Page<CtiVo>> getCtiByPage(@RequestBody CtiQueryRequest ctiQueryRequest, HttpServletRequest request) {
        return null;
    }


}

