package com.yxinmiracle.alsap.controller;

/*
 * @author  YxinMiracle
 * @date  2024-09-05 14:38
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.model.dto.graph.CtiGraphQueryRequest;
import com.yxinmiracle.alsap.model.entity.*;
import com.yxinmiracle.alsap.model.vo.CtiGraphVo;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import com.yxinmiracle.alsap.model.vo.graph.GraphNodeVo;
import com.yxinmiracle.alsap.model.vo.graph.GraphVo;
import com.yxinmiracle.alsap.model.vo.graph.RelationVo;
import com.yxinmiracle.alsap.model.vo.item.ItemVo;
import com.yxinmiracle.alsap.service.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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
        List<Item> itemList = itemService.list();
        List<RelationType> relationTypeList = relationTypeService.list();
        List<Entity> entityList = entityService.list(new LambdaQueryWrapper<Entity>().eq(Entity::getCtiId, ctiId));

        Map<Long, Entity> entityMap = entityList.stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
        Map<Long, Item> itemMap = itemList.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, RelationType> relationTypeMap = relationTypeList.stream().collect(Collectors.toMap(RelationType::getId, Function.identity()));

        List<Relation> dbRelationList = relationService.list(new LambdaQueryWrapper<Relation>().eq(Relation::getCtiId, ctiId));

        // 获取item的信息
        Map<Long, Item> itemId2ItemMap = itemService.getItemId2ItemMap();

        List<GraphVo> CtiGraphVoList = new ArrayList<>();
        for (Relation relation : dbRelationList) {
            Long startCtiEntityId = relation.getStartCtiEntityId();
            Entity startEntity = entityMap.get(startCtiEntityId);
            Long endCtiEntityId = relation.getEndCtiEntityId();
            Entity endEntity = entityMap.get(endCtiEntityId);
            if (ObjectUtils.isEmpty(startCtiEntityId) || ObjectUtils.isEmpty(endCtiEntityId)) {
                continue;
            }

            // 构建头节点Vo，这里的item会是具体的节点类型
            GraphNodeVo startGraphNode = new GraphNodeVo();
            BeanUtils.copyProperties(startEntity, startGraphNode);
            Item tempItem = itemMap.get(startEntity.getItemId());
            ItemVo itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            startGraphNode.setNodeId(startCtiEntityId);
            startGraphNode.setItemData(itemVo);

            // 找到头节点相关的情报数据
            String startEntityName = startEntity.getEntityName();
            Set<Long> startEntityRelatedCtiId = entityService.list(new LambdaQueryWrapper<Entity>().eq(Entity::getEntityName, startEntityName)).stream().map(Entity::getCtiId).filter(filterCtiId -> !Objects.equals(filterCtiId, ctiId)).collect(Collectors.toSet());

            List<CtiVo> startEntityRelatedCti = Optional.ofNullable(startEntityRelatedCtiId).filter(ids -> !ids.isEmpty()).map(ids -> ctiService.getCtiVOList(ctiService.listByIds(ids), itemId2ItemMap)).orElse(Collections.emptyList());
            startGraphNode.setRelatedCti(startEntityRelatedCti);

            // 构建尾节点Vo，这里的item会是具体的节点类型
            GraphNodeVo endGraphNode = new GraphNodeVo();
            BeanUtils.copyProperties(endEntity, endGraphNode);
            tempItem = itemMap.get(endEntity.getItemId());
            itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            endGraphNode.setNodeId(endCtiEntityId);
            endGraphNode.setItemData(itemVo);

            // 找到尾巴节点的相关情报
            String endEntityName = endEntity.getEntityName();
            Set<Long> endEntityRelatedCtiId = entityService.list(new LambdaQueryWrapper<Entity>().eq(Entity::getEntityName, endEntityName)).stream().map(Entity::getCtiId).filter(filterCtiId -> !Objects.equals(filterCtiId, ctiId)).collect(Collectors.toSet());

            List<CtiVo> endEntityRelatedCti = Optional.ofNullable(endEntityRelatedCtiId).filter(ids -> !ids.isEmpty()).map(ids -> ctiService.getCtiVOList(ctiService.listByIds(ids), itemId2ItemMap)).orElse(Collections.emptyList());
            endGraphNode.setRelatedCti(endEntityRelatedCti);

            // 构建关系，这里的item会是stix中的关系类型 1
            Long relationTypeId = relation.getRelationTypeId();
            RelationType dbRelationTypeObj = relationTypeMap.get(relationTypeId);
            Long startNodeFatherItemDbId = dbRelationTypeObj.getStartEntityItemId();
            Long endNodeFatherItemDbId = dbRelationTypeObj.getEndEntityItemId();

            // 构建vo
            Item startFatherNode = itemMap.get(startNodeFatherItemDbId);
            ItemVo startFatherNodeVo = new ItemVo();
            BeanUtils.copyProperties(startFatherNode, startFatherNodeVo);
            // 构建vo
            Item endFatherNode = itemMap.get(endNodeFatherItemDbId);
            ItemVo endFatherNodeVo = new ItemVo();
            BeanUtils.copyProperties(endFatherNode, endFatherNodeVo);
            // 构建关系
            RelationVo relationVo = new RelationVo();
            relationVo.setRelationName(dbRelationTypeObj.getRelationName());
            relationVo.setStartFatherNode(startFatherNodeVo);
            relationVo.setEndFatherNode(endFatherNodeVo);

            GraphVo retObj = new GraphVo();
            retObj.setStartNode(startGraphNode);
            retObj.setEndNode(endGraphNode);
            retObj.setRelation(relationVo);
            CtiGraphVoList.add(retObj);
        }
        return ResultUtils.success(CtiGraphVoList);
    }


}
