package com.yxinmiracle.alsap.controller;

/*
 * @author  YxinMiracle
 * @date  2024-09-05 14:38
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.model.dto.graph.CtiNodeRelCtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.graph.CtiGraphQueryRequest;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import com.yxinmiracle.alsap.model.vo.graph.GraphVo;
import com.yxinmiracle.alsap.model.vo.graph.NodeRelCtiVo;
import com.yxinmiracle.alsap.service.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping("/graph")
@Slf4j
public class GraphController {


    @Resource
    private ItemService itemService;

    @Resource
    private CtiService ctiService;

    @Resource
    private RelationTypeService relationTypeService;

    @Resource
    private RelationService relationService;

    @Resource
    private EntityService entityService;

    @PostMapping("/cti")
    @DecryptRequestBody
    @ApiModelProperty(value = "根据CtiId获取对应的Cti图谱")
    public BaseResponse<List<GraphVo>> getGraphDataByCtiId(@RequestBody CtiGraphQueryRequest ctiGraphQueryRequest, HttpServletRequest request) {
        Long ctiId = ctiGraphQueryRequest.getId();
        if (ObjectUtils.isEmpty(ctiId) || ctiId.equals(0L)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<GraphVo> ctiGraphVoList = ctiService.getGraphDataByCtiId(ctiId);

        return ResultUtils.success(ctiGraphVoList);
    }

    @PostMapping("/node/rel")
    @DecryptRequestBody
    @ApiModelProperty(value = "根据节点id和ctiId找到这个节点相关的情报以及对应的信息")
    public BaseResponse<NodeRelCtiVo> getNodeRelCtiData(@RequestBody CtiNodeRelCtiQueryRequest ctiNodeRelCtiQueryRequest, HttpServletRequest request) {
        Long ctiId = ctiNodeRelCtiQueryRequest.getCtiId();
        String nodeName = ctiNodeRelCtiQueryRequest.getNodeName();
        if (ObjectUtils.isEmpty(ctiId) || StringUtils.isBlank(nodeName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<CtiVo> res = ctiService.getNodeRelCtiData(ctiNodeRelCtiQueryRequest);
        NodeRelCtiVo s = new NodeRelCtiVo();
        s.setRelatedCti(res);
        return ResultUtils.success(s);
    }

}
