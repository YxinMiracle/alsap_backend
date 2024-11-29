package com.yxinmiracle.alsap.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.annotation.AuthCheck;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.annotation.IpFlowLimit;
import com.yxinmiracle.alsap.common.AiServerRet;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.constant.UserConstant;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.*;
import com.yxinmiracle.alsap.model.entity.*;
import com.yxinmiracle.alsap.model.enums.TtpStatusEnum;
import com.yxinmiracle.alsap.model.vo.cti.CtiDetailVo;
import com.yxinmiracle.alsap.model.vo.cti.CtiTtpExtractVo;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import com.yxinmiracle.alsap.model.vo.cti.CtiWordLabelVo;
import com.yxinmiracle.alsap.model.vo.user.NoRoleUserVo;
import com.yxinmiracle.alsap.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * cti接口
 */
@RestController
@RequestMapping("/cti")
@Slf4j
public class CtiController {

    @Resource
    private CtiService ctiService;

    @Resource
    private ItemService itemService;

    @Resource
    private CtiChunkService ctiChunkService;

    @Resource
    private EntityService entityService;

    @Resource
    private RelationService relationService;

    @Resource
    private AiServer aiServer;

    @Resource
    private UserService userService;

    @Resource
    private CtiTtpsService ctiTtpsService;

    // region 增删改查

    /**
     * 根据请求参数来查询数据
     * v1版本接口查询速度约为 811ms
     * 24.11.23功能添加：
     *    1. 首页不再以表格形式展现，使用卡片形式
     *    2. 所以现在需要多返回些内容，返回的内容添加包括：图片的url，情报是否规则
     *    3. 之前接口查询效率较低，现进行效率优化
     *
     * @param ctiQueryRequest
     * @param request
     * @return
     */
//    @PostMapping("/list/page")
//    @ApiOperation(value = "根据Cti查询信息返回CtiVo信息")
//    @DecryptRequestBody
//    @SentinelResource(value = "getCtiByPage",
//            blockHandler = "handleBlockException",
//            fallback = "handleFallback"
//    )
//    public BaseResponse<Page<CtiVo>> getCtiByPage(@RequestBody CtiQueryRequest ctiQueryRequest, HttpServletRequest request) {
//
//        // 获取当前页数以及对应的请求大小
//        long current = ctiQueryRequest.getCurrent();
//        long size = ctiQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//
//        // 先进行分页查询，找到分页后的cti信息
//        Page<Cti> ctiPage = ctiService.page(new Page<>(current, size), ctiService.getQueryWrapper(ctiQueryRequest));
//
//        // 获取item的信息
//        Map<Long, Item> itemId2ItemMap = itemService.getItemId2ItemMap();
//
//        return ResultUtils.success(ctiService.getCtiVOPage(ctiPage, itemId2ItemMap, request));
//    }

    /**
     * 根据请求参数来查询数据
     * v1版本接口查询速度约为 811ms
     * 24.11.23功能添加：
     *    1. 首页不再以表格形式展现，使用卡片形式
     *    2. 所以现在需要多返回些内容，返回的内容添加包括：图片的url，情报是否规则
     *    3. 之前接口查询效率较低，现进行效率优化
     * 24.11.29
     *    1. 限流版
     *
     * @param ctiQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "根据Cti查询信息返回CtiVo信息")
    @DecryptRequestBody
    @IpFlowLimit(resourceName = "getCtiByPageSentinel")
    public BaseResponse<Page<CtiVo>> getCtiByPageSentinel(@RequestBody CtiQueryRequest ctiQueryRequest, HttpServletRequest request) {

        // 获取当前页数以及对应的请求大小
        long current = ctiQueryRequest.getCurrent();
        long size = ctiQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 先进行分页查询，找到分页后的cti信息
        Page<Cti> ctiPage = ctiService.page(new Page<>(current, size), ctiService.getQueryWrapper(ctiQueryRequest));
        // 获取item的信息
        Map<Long, Item> itemId2ItemMap = itemService.getItemId2ItemMap();
        return ResultUtils.success(ctiService.getCtiVOPage(ctiPage, itemId2ItemMap, request));
    }


    /**
     * ctiQueryRequest 流控操作
     * 限流：提示“系统压力过大，请耐心等待”
     */
    public BaseResponse<Page<CtiVo>> handleBlockException(@RequestBody CtiQueryRequest ctiQueryRequest,
                                                                   HttpServletRequest request, BlockException ex) {
        if (ex instanceof DegradeException) {
            return handleFallback(ctiQueryRequest, request, ex);
        }
        // 限流操作
        return ResultUtils.error(ErrorCode.HEIGHT_PRESSURE, "系统压力过大，请耐心等待");
    }

    /**
     * ctiQueryRequest 降级操作：直接返回本地数据
     */
    public BaseResponse<Page<CtiVo>> handleFallback(@RequestBody CtiQueryRequest ctiQueryRequest,
                                                    HttpServletRequest request, Throwable ex) {
        // 可以返回本地数据或空数据
        return ResultUtils.success(null);
    }


    /**
     * 添加Cti情报接口
     *
     * @param ctiAddRequest
     * @param request
     * @return 返回的是CTI情报的ID
     */
    @PostMapping("/add")
    @ApiOperation("添加Cti情报")
    @DecryptRequestBody
    @Transactional
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<Long> addCtiReport(@RequestBody CtiAddRequest ctiAddRequest, HttpServletRequest request) {
        if (ctiAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = new Cti();
        BeanUtils.copyProperties(ctiAddRequest, cti);
        User loginUser = userService.getLoginUser(request);
        cti.setUserId(loginUser.getId());

        if (StringUtils.isAnyBlank(ctiAddRequest.getContent(), ctiAddRequest.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = ctiService.save(cti);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 添加一个cti的ttp初始信息，随后我
        // 先进行判定，看看数据库中是否存在这个对象，要是存在直接报错
        int ctiTtpInDbCount = ctiTtpsService.list(new LambdaQueryWrapper<CtiTtps>().eq(CtiTtps::getCtiId, cti.getId())).size();
        ThrowUtils.throwIf(ctiTtpInDbCount > 0, ErrorCode.PARAMS_ERROR);

        CtiTtps ctiTtps = new CtiTtps();
        ctiTtps.setCtiId(cti.getId());
        ctiTtps.setStatus(TtpStatusEnum.PROCESSING.getValue()); // 更新初始化的状态，更改为正在处理中

        ctiTtpsService.save(ctiTtps);

        // 调用异步方法添加ttp数据
        CompletableFuture.runAsync(() -> {
            // 该请求没有返回值，ai服务处理好之后会调用后端add ttp
            // 要是这里请求出现错误，那么就更新状态为需要后续进行重试，或者用户手动进行重试请求。
            CtiTtpExtractVo ctiTtpExtractVo = CtiTtpExtractVo.builder().content(cti.getContent()).ctiId(cti.getId()).build();
            AiServerRet aiServerRet = aiServer.getCtiContentTtp(ctiTtpExtractVo);
            if (aiServerRet.getCode() != ErrorCode.SUCCESS.getCode()) {
                ctiTtps.setStatus(TtpStatusEnum.Retrying.getValue());
                ctiTtpsService.updateById(ctiTtps);
            }

            // 获取实体数据并构图
            CtiEntityExtractRequest ctiEntityExtractRequest = new CtiEntityExtractRequest();
            ctiEntityExtractRequest.setContent(cti.getContent());
            ctiEntityExtractRequest.setCtiId(cti.getId());
            aiServer.getCtiEntityAndGraph(ctiEntityExtractRequest);

            // 异步请求摘要信息
            CtiAbstractRequest ctiAbstractRequest = new CtiAbstractRequest();
            ctiAbstractRequest.setContent(cti.getContent());
            AiServerRet ctiAbstract = aiServer.getCtiAbstract(ctiAbstractRequest);
            String data = ctiAbstract.getData();
            cti.setAbstractText(data);
            ctiService.updateById(cti);
        });

        return ResultUtils.success(cti.getId());
    }

//    @GetMapping("/add_test")
//    public BaseResponse<Long> addCtiReport2(HttpServletRequest request) {
//        Cti tempCti = ctiService.getById(1789287766325039106L);// 模拟id
//
//        // 添加一个cti的ttp初始信息，随后我
//        // 先进行判定，看看数据库中是否存在这个对象，要是存在直接报错
//        int ctiTtpInDbCount = ctiTtpsService.list(new LambdaQueryWrapper<CtiTtps>().eq(CtiTtps::getCtiId, tempCti.getId())).size();
//        ThrowUtils.throwIf(ctiTtpInDbCount > 0, ErrorCode.PARAMS_ERROR);
//
//        CtiTtps ctiTtps = new CtiTtps();
//        ctiTtps.setCtiId(tempCti.getId());
//        ctiTtps.setStatus(TtpStatusEnum.PROCESSING.getValue()); // 更新初始化的状态，更改为正在处理中
//
//        ctiTtpsService.save(ctiTtps);
//        // 调用异步方法
//        CompletableFuture.runAsync(() -> {
//            // 该请求没有返回值，ai服务处理好之后会调用后端add ttp
//            // 要是这里请求出现错误，那么就更新状态为需要后续进行重试，或者用户手动进行重试请求。
//            CtiTtpExtractVo ctiTtpExtractVo = CtiTtpExtractVo.builder().content(tempCti.getContent()).ctiId(tempCti.getId()).build();
//            AiServerRet aiServerRet = aiServer.getCtiContentTtp(ctiTtpExtractVo);
//            if (aiServerRet.getCode() != ErrorCode.SUCCESS.getCode()) {
//                ctiTtps.setStatus(TtpStatusEnum.Retrying.getValue());
//                ctiTtpsService.updateById(ctiTtps);
//            }
//        });
//
//        return ResultUtils.success(tempCti.getId());
//    }
//
//    @GetMapping("/test/cti/add")
//    public BaseResponse<Long> addTestCti(HttpServletRequest request) {
//        List<Cti> list1 = ctiService.list();
//        for (Cti cti : list1) {
//            List<CtiChunk> list = ctiChunkService.list(new LambdaQueryWrapper<CtiChunk>().eq(CtiChunk::getCtiId, cti.getId()));
//            if (list.isEmpty()){
//                Cti tempCti = ctiService.getById(cti.getId());// 模拟id
//                CtiEntityExtractRequest ctiEntityExtractRequest = new CtiEntityExtractRequest();
//                ctiEntityExtractRequest.setContent(tempCti.getContent());
//                ctiEntityExtractRequest.setCtiId(tempCti.getId());
//                // 调用异步方法
//                CompletableFuture.runAsync(() -> {
//                    aiServer.getCtiEntityAndGraph(ctiEntityExtractRequest);
//                });
//            }
//        }

//        List<Cti> list1 = ctiService.list(new LambdaUpdateWrapper<Cti>().orderByDesc(Cti::getCreateTime));
//        for (Cti cti : list1.subList(31, list1.size()-1)) {
//            CtiTtps ctiTtps = new CtiTtps();
//            ctiTtps.setCtiId(cti.getId());
//            ctiTtps.setStatus(TtpStatusEnum.PROCESSING.getValue()); // 更新初始化的状态，更改为正在处理中
//
//            ctiTtpsService.save(ctiTtps);
//            // 调用异步方法
//            // 该请求没有返回值，ai服务处理好之后会调用后端add ttp
//            // 要是这里请求出现错误，那么就更新状态为需要后续进行重试，或者用户手动进行重试请求。
//            CtiTtpExtractVo ctiTtpExtractVo = CtiTtpExtractVo.builder().content(cti.getContent()).ctiId(cti.getId()).build();
//            AiServerRet aiServerRet = aiServer.getCtiContentTtp(ctiTtpExtractVo);
//            if (aiServerRet.getCode() != ErrorCode.SUCCESS.getCode()) {
//                ctiTtps.setStatus(TtpStatusEnum.Retrying.getValue());
//                ctiTtpsService.updateById(ctiTtps);
//            }
//        }
//        return ResultUtils.success(11L);
//    }

//    @GetMapping("/test/cti/abstract")
//    public BaseResponse<Long> addAbstractCti(HttpServletRequest request) {
//        List<Cti> list1 = ctiService.list(new LambdaQueryWrapper<Cti>().orderByDesc(Cti::getUpdateTime));
//        for (Cti cti : list1) {
//            if (StringUtils.isBlank(cti.getAbstractText())) {
//                CtiAbstractRequest ctiAbstractRequest = new CtiAbstractRequest();
//                ctiAbstractRequest.setContent(cti.getContent());
//                AiServerRet ctiAbstract = aiServer.getCtiAbstract(ctiAbstractRequest);
//                String data = ctiAbstract.getData();
//                System.out.println(">>>>>>>>>>>>>>>" + " " + data);
//                cti.setAbstractText(data);
//                ctiService.updateById(cti);
//            }
//        }
//        return ResultUtils.success(11L);
//    }


    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "根据CtiId删除对应的Cti，需要管理员权限")
    @DecryptRequestBody
    @Transactional
    public BaseResponse<Long> deleteCtiByCtiId(@RequestBody CtiDeleteRequest ctiDeleteRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(ctiDeleteRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = new Cti();
        BeanUtils.copyProperties(ctiDeleteRequest, cti);
        boolean result = ctiService.removeById(cti);
        boolean ctiChunkResult = ctiChunkService.remove(new LambdaQueryWrapper<CtiChunk>().eq(CtiChunk::getCtiId, cti.getId()));
        boolean entityResult = entityService.remove(new LambdaQueryWrapper<Entity>().eq(Entity::getCtiId, cti.getId()));
        boolean relationResult = relationService.remove(new LambdaQueryWrapper<com.yxinmiracle.alsap.model.entity.Relation>().eq(Relation::getCtiId, cti.getId()));
        ThrowUtils.throwIf(!(result && ctiChunkResult && entityResult && relationResult), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(cti.getId());
    }

    @PostMapping("/word/label/page/")
    @DecryptRequestBody
    @ApiOperation(value = "获取模型已经标注好的数据")
    public BaseResponse<CtiWordLabelVo> getPageCtiWordLabelList(@RequestBody CtiModelWordLabelResultDto ctiModelWordLabelResultDto, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(ctiModelWordLabelResultDto)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (ctiModelWordLabelResultDto.getCurrent() <= 0 || ctiModelWordLabelResultDto.getSize() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long ctiId = ctiModelWordLabelResultDto.getCtiId();
        Integer current = ctiModelWordLabelResultDto.getCurrent();
        Integer size = ctiModelWordLabelResultDto.getSize();

        Cti cti = ctiService.getById(ctiId);
        if (ObjectUtils.isEmpty(cti.getWordList())) {
            throw new BusinessException(ErrorCode.EMPTY_DATA);
        }

        CtiWordLabelVo ctiWordLabelVo = new CtiWordLabelVo();

        Gson gson = new Gson();
        Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        List<List<String>> wordList = gson.fromJson(cti.getWordList(), type);
        List<List<String>> labelList = gson.fromJson(cti.getLabelList(), type);

        int dataSize = wordList.size();
        int start = (current - 1) * size;
        int end = current * size;
        ThrowUtils.throwIf(start > dataSize, ErrorCode.PARAMS_ERROR);
        end = Math.min(end, dataSize);
        ArrayList<List<String>> pageWordList = new ArrayList<>(wordList.subList(start, end));
        ArrayList<List<String>> pageLabelList = new ArrayList<>(labelList.subList(start, end));
        ctiWordLabelVo.setWordList(pageWordList);
        ctiWordLabelVo.setLabelList(pageLabelList);
        ctiWordLabelVo.setTotal(dataSize);
        return ResultUtils.success(ctiWordLabelVo);
    }


    /**
     * cti详情界面的数据返回，这里是包括实体的
     *
     * @param ctiDetailQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/detail")
    @DecryptRequestBody
    @ApiOperation(value = "根据ctiId获取对应的cti详情信息")
    public BaseResponse<CtiDetailVo> getDetailCti(@RequestBody CtiDetailQueryRequest ctiDetailQueryRequest, HttpServletRequest request) {
        Long ctiId = ctiDetailQueryRequest.getId();
        if (ObjectUtils.isEmpty(ctiId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Cti cti = ctiService.getById(ctiId);
        CtiDetailVo ctiDetailVo = new CtiDetailVo();
        BeanUtils.copyProperties(cti, ctiDetailVo);
        List<CtiChunk> ctiChunkList = ctiChunkService.list(new LambdaQueryWrapper<CtiChunk>().eq(CtiChunk::getCtiId, cti.getId()));
        ctiDetailVo.setCtiChunkList(ctiChunkList);
        ctiDetailVo.setEntityNum(ctiChunkList.size());

        // 获取sco和sdo的信息
        Map<Long, Item> itemId2ItemMap = itemService.getItemId2ItemMap();
        CtiVo scoAndSdoCount = ctiService.getScoAndSdoCountByCtiId(ctiId, itemId2ItemMap);
        ctiDetailVo.setScoNum(scoAndSdoCount.getScoNum());
        ctiDetailVo.setSdoNum(scoAndSdoCount.getSdoNum());

        // 获取创建该情报的用户
        Long userId = cti.getUserId();
        User user = userService.getById(userId);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCode.NOT_FOUND_ERROR, "非法情报来源");
        NoRoleUserVo noRoleUserVo = new NoRoleUserVo();
        BeanUtils.copyProperties(user, noRoleUserVo);
        ctiDetailVo.setUser(noRoleUserVo);

        return ResultUtils.success(ctiDetailVo);
    }


}
