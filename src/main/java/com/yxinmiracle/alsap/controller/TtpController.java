package com.yxinmiracle.alsap.controller;

/*
 * @author  YxinMiracle
 * @date  2024-09-17 9:41
 * @Gitee: https://gitee.com/yxinmiracle
 */

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.utils.IOUtils;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.manager.CosManager;
import com.yxinmiracle.alsap.model.dto.cti.CtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.ttp.TtpQueryRequest;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.CtiTtps;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import com.yxinmiracle.alsap.service.CtiTtpsService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 情报ttp接口
 */
@RestController
@RequestMapping("/ttp")
@Slf4j
public class TtpController {

    @Resource
    private CtiTtpsService ctiTtpsService;

    @Resource
    private CosManager cosManager;

    /**
     * 根据请求参数来查询数据
     *
     * @param ttpQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/config")
    @ApiOperation(value = "根据Cti查询信息返回CtiVo信息")
    @DecryptRequestBody
    public BaseResponse<String> getTtpConfigByCtiId(@RequestBody TtpQueryRequest ttpQueryRequest, HttpServletRequest request) {
        if (ttpQueryRequest == null || ttpQueryRequest.getCtiId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CtiTtps ctiTtps = ctiTtpsService.getOne(new LambdaQueryWrapper<CtiTtps>().eq(CtiTtps::getCtiId, ttpQueryRequest.getCtiId()));
        return ResultUtils.success(ctiTtps.getSavaPath());
    }

    @GetMapping("/download")
    public void downloadTtpConfigById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        CtiTtps ctiTtps = ctiTtpsService.getOne(new LambdaQueryWrapper<CtiTtps>().eq(CtiTtps::getCtiId, id));

        if (ctiTtps == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String filePath = ctiTtps.getSavaPath();
        if (StringUtils.isBlank(filePath)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "配置文件不存在");
        }

        // 日志记录
        log.info("用户下载了 {}", filePath);

        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filePath);

        InputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObj(filePath);
            cosObjectInput = cosObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            // 写入相应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 用完流之后一定要调用 close()
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

}

