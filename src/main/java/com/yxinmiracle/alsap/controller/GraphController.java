package com.yxinmiracle.alsap.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.*;
import com.yxinmiracle.alsap.model.entity.*;
import com.yxinmiracle.alsap.model.enums.EntityTypeEnum;
import com.yxinmiracle.alsap.model.vo.*;
import com.yxinmiracle.alsap.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
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
    private UserService userService;

    @Resource
    private CtiService ctiService;

    @Resource
    private CtiChunkService ctiChunkService;

    @Resource
    private RelationTypeService relationTypeService;

    @Resource
    private RelationService relationService;

    @Resource
    private EntityService entityService;


    @GetMapping("/{ctiId}")
    public BaseResponse<List<CtiGraphVo>> getGraphDataByCtiId(@PathVariable("ctiId") Long ctiId, HttpServletRequest request) {
        List<Item> itemList = itemService.list();
        List<RelationType> relationTypeList = relationTypeService.list();
        List<Entity> entityList = entityService.list(new LambdaQueryWrapper<Entity>().eq(Entity::getCtiId, ctiId));
        List<Cti> ctiList = ctiService.list();
        Map<Long, Cti> ctiMap = ctiList.stream().collect(Collectors.toMap(Cti::getId, Function.identity()));
        Map<Long, Entity> entityMap = entityList.stream()
                .collect(Collectors.toMap(Entity::getId, Function.identity()));
        Map<Long, Item> itemMap = itemList.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, RelationType> relationTypeMap = relationTypeList.stream()
                .collect(Collectors.toMap(RelationType::getId, Function.identity()));

        List<Relation> dbRelationList = relationService.list(new LambdaQueryWrapper<Relation>().eq(Relation::getCtiId, ctiId));

        List<CtiGraphVo> CtiGraphVoList = new ArrayList<>();
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
            startGraphNode.setEntityName(startEntity.getEntityName());
            startGraphNode.setCtiId(startEntity.getCtiId());
            startGraphNode.setCtiName(ctiMap.get(ctiId).getTitle());
            Item tempItem = itemMap.get(startEntity.getItemId());
            ItemVo itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            startGraphNode.setNodeId(startCtiEntityId);
            startGraphNode.setItemData(itemVo);

            // 构建尾节点Vo，这里的item会是具体的节点类型
            GraphNodeVo endGraphNode = new GraphNodeVo();
            endGraphNode.setEntityName(endEntity.getEntityName());
            endGraphNode.setCtiId(endEntity.getCtiId());
            endGraphNode.setCtiName(ctiMap.get(ctiId).getTitle());
            tempItem = itemMap.get(endEntity.getItemId());
            itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            endGraphNode.setNodeId(endCtiEntityId);
            endGraphNode.setItemData(itemVo);

            // 构建关系，这里的item会是stix中的关系类型
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

            CtiGraphVo retObj = new CtiGraphVo();
            retObj.setStartNode(startGraphNode);
            retObj.setEndNode(endGraphNode);
            retObj.setRelation(relationVo);
            CtiGraphVoList.add(retObj);
        }
        return ResultUtils.success(CtiGraphVoList);
    }

    @PostMapping("/node/search")
    public BaseResponse<List<GraphNodeVo>> getItemByNameAndEntityType(@RequestBody EntityQueryRequest entityQueryRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(entityQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String entityName = entityQueryRequest.getEntityName();  //  这里是确定实体名称的
        Integer itemId = entityQueryRequest.getItemId();  // 这里是确定实体类型的

        LambdaQueryWrapper<Entity> entityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        entityLambdaQueryWrapper.eq(Entity::getEntityName, entityName);
        if (itemId != 0) {
            entityLambdaQueryWrapper.eq(Entity::getItemId, itemId);
        }
        List<Item> itemList = itemService.list();
        Map<Long, Item> itemMap = itemList.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        List<Cti> ctiList = ctiService.list();
        Map<Long, Cti> ctiMap = ctiList.stream().collect(Collectors.toMap(Cti::getId, Function.identity()));

        List<Entity> entityList = entityService.list(entityLambdaQueryWrapper); // 这里会搜索出一些数据，这是会大于1个的
        List<GraphNodeVo> graphNodeVoList = new ArrayList<>();  // 这是用作存储最后的返回数据的

        for (Entity entity : entityList) {
            GraphNodeVo graphNodeVo = new GraphNodeVo();
            graphNodeVo.setCtiId(entity.getCtiId());
            graphNodeVo.setNodeId(entity.getId());
            graphNodeVo.setEntityName(entity.getEntityName());
            graphNodeVo.setCtiName(ctiMap.get(entity.getCtiId()).getTitle());
            ItemVo itemVo = new ItemVo();
            Item item = itemMap.get(entity.getItemId());
            BeanUtils.copyProperties(item, itemVo);
            graphNodeVo.setItemData(itemVo);
            graphNodeVoList.add(graphNodeVo);
        }
        return ResultUtils.success(graphNodeVoList);
    }

    @PostMapping("/prevent/consider")
    public BaseResponse<List<CtiGraphVo>> addConsiderableItemInGraph(@RequestBody PreventGraphQueryRequest preventGraphQueryRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(preventGraphQueryRequest) || preventGraphQueryRequest.getGraphData().size() == 0) { // 确保参数不为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Long> consideredItemId = preventGraphQueryRequest.getConsideredItemId();
        List<CtiGraphVo> graphData = preventGraphQueryRequest.getGraphData();
        List<Entity> entityList = entityService.list();
        Map<Long, Entity> entityMap = entityList.stream()
                .collect(Collectors.toMap(Entity::getId, Function.identity()));
        // 1. 所有的节点放在一个set里面
        Set<Long> startNodeIdSet = new HashSet<>();
        for (CtiGraphVo graphDatum : graphData) {
            GraphNodeVo startNode = graphDatum.getStartNode();
            GraphNodeVo endNode = graphDatum.getEndNode();
            startNodeIdSet.add(startNode.getNodeId());
            startNodeIdSet.add(endNode.getNodeId());
        }
        List<Long> startNodeIdList = new ArrayList<>(startNodeIdSet);
        PreventEntityQuery preventEntityQuery = new PreventEntityQuery();
        preventEntityQuery.setStartNodeIdList(startNodeIdList);
        preventEntityQuery.setItemTypeIds(consideredItemId);

        List<Relation> relationList = ctiService.getPreventGraphRelationDataList(preventEntityQuery);

        // 开始进行构建
        List<Item> itemList = itemService.list();
        List<RelationType> relationTypeList = relationTypeService.list();
        List<Cti> ctiList = ctiService.list();
        Map<Long, Cti> ctiMap = ctiList.stream().collect(Collectors.toMap(Cti::getId, Function.identity()));
        Map<Long, Item> itemMap = itemList.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, RelationType> relationTypeMap = relationTypeList.stream()
                .collect(Collectors.toMap(RelationType::getId, Function.identity()));

        List<CtiGraphVo> CtiGraphVoList = new ArrayList<>();
        for (Relation relation : relationList) {
            Long startCtiEntityId = relation.getStartCtiEntityId();
            Entity startEntity = entityMap.get(startCtiEntityId);
            Long endCtiEntityId = relation.getEndCtiEntityId();
            Entity endEntity = entityMap.get(endCtiEntityId);
            if (ObjectUtils.isEmpty(startCtiEntityId) || ObjectUtils.isEmpty(endCtiEntityId)) {
                continue;
            }
            // 构建头节点Vo，这里的item会是具体的节点类型
            GraphNodeVo startGraphNode = new GraphNodeVo();
            startGraphNode.setEntityName(startEntity.getEntityName());
            startGraphNode.setCtiId(startEntity.getCtiId());
            startGraphNode.setCtiName(ctiMap.get(startEntity.getCtiId()).getTitle());
            Item tempItem = itemMap.get(startEntity.getItemId());
            ItemVo itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            startGraphNode.setNodeId(startCtiEntityId);
            startGraphNode.setItemData(itemVo);

            // 构建尾节点Vo，这里的item会是具体的节点类型
            GraphNodeVo endGraphNode = new GraphNodeVo();
            endGraphNode.setEntityName(endEntity.getEntityName());
            endGraphNode.setCtiId(endEntity.getCtiId());
            endGraphNode.setCtiName(ctiMap.get(endEntity.getCtiId()).getTitle());
            tempItem = itemMap.get(endEntity.getItemId());
            itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            endGraphNode.setNodeId(endCtiEntityId);
            endGraphNode.setItemData(itemVo);

            // 构建关系，这里的item会是stix中的关系类型
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

            CtiGraphVo retObj = new CtiGraphVo();
            retObj.setStartNode(startGraphNode);
            retObj.setEndNode(endGraphNode);
            retObj.setRelation(relationVo);
            CtiGraphVoList.add(retObj);
        }
        return ResultUtils.success(CtiGraphVoList);
    }

    public List<Relation> bfs(List<Relation> relationList, Long startId, int jumpNum,Map<Long, List<Relation>> graph) {
        List<Relation> result = new ArrayList<>();

        // BFS
        Queue<Long> queue = new LinkedList<>();
        queue.offer(startId);
        Set<Long> visited = new HashSet<>();
        visited.add(startId);
        int currentLevel = 0;

        while (!queue.isEmpty() && (jumpNum == -1 || currentLevel <= jumpNum)) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Long currentId = queue.poll();
                if (!graph.containsKey(currentId)) {
                    continue;
                }
                for (Relation relation : graph.get(currentId)) {
                    Long neighborId = relation.getEndCtiEntityId().equals(currentId) ? relation.getStartCtiEntityId() : relation.getEndCtiEntityId();
//                    Long neighborId = relation.getEndCtiEntityId();
                    if (!visited.contains(neighborId)) {
                        visited.add(neighborId);
                        queue.offer(neighborId);
                        result.add(relation);
                    }
                }
            }
            currentLevel++;
        }
        return result;
    }

    @PostMapping("/jump/graph")
    public BaseResponse<List<CtiGraphVo>> getEntityGraphByJumpNum(@RequestBody EntityJumpNumQuery entityJumpNumQuery, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(entityJumpNumQuery)) { // 确保参数不为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<Long, List<Relation>> graph = new HashMap<>();

        List<Entity> entities = entityService.list(new LambdaQueryWrapper<Entity>().eq(Entity::getEntityName, entityJumpNumQuery.getEntityName()));
        List<Long> entityIdList = entities.stream().map(Entity::getId).collect(Collectors.toList());

        Integer jumpNum = entityJumpNumQuery.getJumpNum();
        List<Relation> relationList = relationService.list();

        List<Relation> result = new ArrayList<>();
        // 构建图，确保每个ID作为起点和终点都被处理
        for (Relation relation : relationList) {
            if (relation.getStartCtiEntityId() != null && relation.getEndCtiEntityId() != null) {
                graph.computeIfAbsent(relation.getStartCtiEntityId(), k -> new ArrayList<>()).add(relation);
                graph.computeIfAbsent(relation.getEndCtiEntityId(), k -> new ArrayList<>()).add(relation);
            }
        }
        for (Long entityId : entityIdList) {
            List<Relation> relations = bfs(relationList, entityId, jumpNum, graph);
            result.addAll(relations);
        }

        // 开始进行构建
        List<Item> itemList = itemService.list();
        List<RelationType> relationTypeList = relationTypeService.list();
        List<Cti> ctiList = ctiService.list();
        List<Entity> entityList = entityService.list();

        Map<Long, Cti> ctiMap = ctiList.stream().collect(Collectors.toMap(Cti::getId, Function.identity()));
        Map<Long, Item> itemMap = itemList.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, RelationType> relationTypeMap = relationTypeList.stream()
                .collect(Collectors.toMap(RelationType::getId, Function.identity()));
        Map<Long, Entity> entityMap = entityList.stream()
                .collect(Collectors.toMap(Entity::getId, Function.identity()));

        List<CtiGraphVo> ctiGraphVoList = new ArrayList<>();
        for (Relation relation : result) {
            Long startCtiEntityId = relation.getStartCtiEntityId();
            Entity startEntity = entityMap.get(startCtiEntityId);
            Long endCtiEntityId = relation.getEndCtiEntityId();
            Entity endEntity = entityMap.get(endCtiEntityId);
            if (ObjectUtils.isEmpty(startCtiEntityId) || ObjectUtils.isEmpty(endCtiEntityId)) {
                continue;
            }

            // 构建头节点Vo，这里的item会是具体的节点类型
            GraphNodeVo startGraphNode = new GraphNodeVo();
            startGraphNode.setEntityName(startEntity.getEntityName());
            startGraphNode.setCtiId(startEntity.getCtiId());
            startGraphNode.setCtiName(ctiMap.get(startEntity.getCtiId()).getTitle());
            Item tempItem = itemMap.get(startEntity.getItemId());
            ItemVo itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            startGraphNode.setNodeId(startCtiEntityId);
            startGraphNode.setItemData(itemVo);

            // 构建尾节点Vo，这里的item会是具体的节点类型
            GraphNodeVo endGraphNode = new GraphNodeVo();
            endGraphNode.setEntityName(endEntity.getEntityName());
            endGraphNode.setCtiId(endEntity.getCtiId());
            endGraphNode.setCtiName(ctiMap.get(endGraphNode.getCtiId()).getTitle());
            tempItem = itemMap.get(endEntity.getItemId());
            itemVo = new ItemVo();
            BeanUtils.copyProperties(tempItem, itemVo);
            endGraphNode.setNodeId(endCtiEntityId);
            endGraphNode.setItemData(itemVo);

            // 构建关系，这里的item会是stix中的关系类型
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

            CtiGraphVo retObj = new CtiGraphVo();
            retObj.setStartNode(startGraphNode);
            retObj.setEndNode(endGraphNode);
            retObj.setRelation(relationVo);
            ctiGraphVoList.add(retObj);
        }
        return ResultUtils.success(ctiGraphVoList);
    }

}
