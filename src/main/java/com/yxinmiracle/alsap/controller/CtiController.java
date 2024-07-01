package com.yxinmiracle.alsap.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.annotation.AuthCheck;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.constant.UserConstant;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.*;
import com.yxinmiracle.alsap.model.dto.model.ModelCtiVo;
import com.yxinmiracle.alsap.model.entity.*;
import com.yxinmiracle.alsap.model.enums.EntityTypeEnum;
import com.yxinmiracle.alsap.model.vo.*;
import com.yxinmiracle.alsap.service.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/cti")
@Slf4j
public class CtiController {

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

    @GetMapping("/getItem")
    public BaseResponse<List<Item>> getItems(HttpServletRequest request) {
        List<Item> itemList = itemService.list();
        if (itemList == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(itemList);
    }

    public Map<Long, Integer> getItemTypeIdMap() {
        Map<Long, Integer> resMap = new HashMap<>();
        List<Item> list = itemService.list();
        for (Item item : list) {
            resMap.put(item.getId(), (int) item.getItemType());
        }
        return resMap;
    }

    /**
     * 添加唯一cti情报中的唯一实体
     *
     * @return
     */
    @PostMapping("/add/unique/entity")
    public BaseResponse<Long> addUniqueEntity(@RequestBody EntityAddRequest entityAddRequest) {
        if (ObjectUtils.isEmpty(entityAddRequest)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Entity entity = new Entity();
        BeanUtils.copyProperties(entityAddRequest, entity);
        Entity dbEntity = entityService.getOne(new LambdaQueryWrapper<Entity>()
                .eq(Entity::getCtiId, entity.getCtiId())
                .eq(Entity::getEntityName, entity.getEntityName())
                .eq(Entity::getItemId, entity.getItemId()));
        // 我需要判断这个实体在不在数据库了，在数据库我就返回这个在数据库里面的实体id，并不做插入
        if (ObjectUtils.isNotEmpty(dbEntity)) {
            // 表示这个实体并不存在数据库中
            return ResultUtils.success(dbEntity.getId());
        }
        // 如果没有走上面，那么就表示需要进行实体的插入
        boolean result = entityService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(entity.getId());
    }

    /**
     * 这里是为了通过ctiId和对应的entityName去获得到对应的实体id，在关系添加的时候需要用到
     *
     * @param ctiId
     * @param entityName
     * @param request
     * @return
     */
    @GetMapping("/unique/entityId/{ctiId}/{entityName}/{itemId}")
    public BaseResponse<Long> getUniqueEntityIdByCtiIdAndEntityName(@PathVariable("ctiId") Long ctiId,
                                                                    @PathVariable("entityName") String entityName,
                                                                    @PathVariable("itemId") Long itemId,
                                                                    HttpServletRequest request) {
        if (ObjectUtils.isEmpty(ctiId) || StringUtils.isBlank(entityName)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Entity entity = entityService.getOne(new LambdaQueryWrapper<Entity>()
                .eq(Entity::getCtiId, ctiId)
                .eq(Entity::getEntityName, entityName)
                .eq(Entity::getItemId, itemId));
//        System.out.println();
        ThrowUtils.throwIf(!ObjectUtils.isNotEmpty(entity), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(entity.getId());
    }

    @PostMapping("/list/cti/page")
    public BaseResponse<Page<CtiVo>> getCtiByPage(@RequestBody CtiQueryRequest ctiQueryRequest, HttpServletRequest request) {
        // 获取当前页数以及对应的请求大小
        long current = ctiQueryRequest.getCurrent();
        long size = ctiQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<Cti> ctiPage = ctiService.page(new Page<>(current, size),
                ctiService.getQueryWrapper(ctiQueryRequest));

        List<CtiVo> ctiVoList = new ArrayList<>();
        Map<Long, Integer> itemTypeIdMap = getItemTypeIdMap();


        for (Cti cti : ctiPage.getRecords()) {
            CtiVo ctiVo = new CtiVo();
            BeanUtils.copyProperties(cti, ctiVo);
            // 获取到的是所有的关于这个CtiI对的itemId表
            List<Long> ctiItemIdList = ctiChunkService.list(new LambdaQueryWrapper<CtiChunk>()
                    .eq(CtiChunk::getCtiId, cti.getId()))
                    .stream()
                    .map(CtiChunk::getItemId).collect(Collectors.toList());

            ctiVo.setHasGraph(relationService.count(new LambdaQueryWrapper<Relation>().eq(Relation::getCtiId, cti.getId())) > 0 ? 1 : 0);

            // 判断它对应的item是不是空的，很有可能是空的
            long sdoCount = 0L;
            long scoCount = 0L;
            if (!ctiItemIdList.isEmpty()) {
                sdoCount = ctiItemIdList.stream()
                        .filter(itemId -> itemTypeIdMap.get(itemId) == EntityTypeEnum.SDO.getValue())
                        .count();
                scoCount = ctiItemIdList.stream()
                        .filter(itemId -> itemTypeIdMap.get(itemId) == EntityTypeEnum.SCO.getValue())
                        .count();
            }
            ctiVo.setEntityNum((int) (scoCount + sdoCount));
            ctiVo.setSdoNum((int) sdoCount);
            ctiVo.setScoNum((int) scoCount);
            ctiVoList.add(ctiVo);
        }
        Page<CtiVo> ctiVoPage = new Page<>(ctiPage.getCurrent(), ctiPage.getSize(), ctiPage.getTotal());
        ctiVoPage.setRecords(ctiVoList);
        return ResultUtils.success(ctiVoPage);
    }

    /**
     * cti详情界面的数据返回，这里是包括实体的
     *
     * @param ctiId
     * @param request
     * @return
     */
    @GetMapping("/detail/{ctiId}")
    public BaseResponse<CtiDetailVo> getDetailCti(@PathVariable("ctiId") Long ctiId, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(ctiId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Cti cti = ctiService.getById(ctiId);
        CtiDetailVo ctiDetailVo = new CtiDetailVo();
        BeanUtils.copyProperties(cti, ctiDetailVo);
        List<CtiChunk> ctiChunkList = ctiChunkService.list(new LambdaQueryWrapper<CtiChunk>().eq(CtiChunk::getCtiId, cti.getId()));
        ctiDetailVo.setCtiChunkList(ctiChunkList);
        ctiDetailVo.setEntityNum(ctiChunkList.size());
        ctiDetailVo.setWordList(new ArrayList<>());
        ctiDetailVo.setLabelList(new ArrayList<>());
        return ResultUtils.success(ctiDetailVo);
    }


    @GetMapping("/word/label/page/{ctiId}/{current}/{size}")
    public BaseResponse<CtiDetailVo> getPageCtiWordLabelList(@PathVariable("ctiId") Long ctiId, @PathVariable("current") Integer current,
                                                             @PathVariable("size") Integer size, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(ctiId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (current <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = ctiService.getById(ctiId);

        CtiDetailVo ctiDetailVo = new CtiDetailVo();

        Gson gson = new Gson();
        Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        List<List<String>> wordList = gson.fromJson(cti.getWordList(), type);
        List<List<String>> labelList = gson.fromJson(cti.getLabelList(), type);
        int dataSize = wordList.size();
        int start = (current - 1) * size;
        int end = current * size;
        if (start > dataSize) {
            ctiDetailVo.setWordList(new ArrayList<>());
            ctiDetailVo.setLabelList(new ArrayList<>());
            return ResultUtils.success(ctiDetailVo);
        }
        end = Math.min(end, dataSize);

        ArrayList<List<String>> pageWordList = new ArrayList<>(wordList.subList(start, end));
        ArrayList<List<String>> pageLabelList = new ArrayList<>(labelList.subList(start, end));
        ctiDetailVo.setWordList(pageWordList);
        ctiDetailVo.setLabelList(pageLabelList);
        return ResultUtils.success(ctiDetailVo);
    }

    /**
     * 根据cti的名称返回对应的id
     *
     * @param title
     * @param request
     * @return
     */
    @GetMapping("/getCtiId/byName/{title}")
    public BaseResponse<Long> getCtiId(@PathVariable("title") String title, HttpServletRequest request) {
        if (StringUtils.isBlank(title)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Cti cti = ctiService.getOne(new LambdaQueryWrapper<Cti>().eq(Cti::getTitle, title));
        return ResultUtils.success(cti.getId());
    }

    @PostMapping("/add/relation")
    public BaseResponse<Long> addRelationData(@RequestBody RelationAddRequest relationAddRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(relationAddRequest)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Relation relation = new Relation();
        BeanUtils.copyProperties(relationAddRequest, relation);
        Relation dbRelation = relationService.getOne(new LambdaQueryWrapper<Relation>().eq(Relation::getCtiId, relation.getCtiId())
                .eq(Relation::getRelationTypeId, relation.getRelationTypeId())
                .eq(Relation::getStartCtiEntityId, relation.getStartCtiEntityId())
                .eq(Relation::getEndCtiEntityId, relation.getEndCtiEntityId()));
        if (!ObjectUtils.isEmpty(dbRelation)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = relationService.save(relation);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(relation.getId());
    }

    /**
     * 根据item的id返回详细的id信息
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/item/{id}")
    public BaseResponse<Item> getItemById(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Item item = itemService.getById(id);
        return ResultUtils.success(item);
    }

    /**
     * 根据item的名称，返回item的id
     *
     * @param itemName
     * @param request
     * @return
     */
    @GetMapping("/get/itemId/{itemName}")
    public BaseResponse<Long> getItemIdByItemName(@PathVariable("itemName") String itemName, HttpServletRequest request) {
        if (StringUtils.isBlank(itemName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Item resItem = itemService.getOne(new QueryWrapper<Item>().eq("itemName", itemName));
        return ResultUtils.success(resItem.getId());
    }

    /**
     * 根据item的子类名称，返回父类item的id
     *
     * @param itemName
     * @param request
     * @return
     */
    @GetMapping("/get/itemId/itemContent/{itemName}")
    public BaseResponse<Long> getItemIdByItemContent(@PathVariable("itemName") String itemName, HttpServletRequest request) {
        if (StringUtils.isBlank(itemName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Item resItem = itemService.getOne(new QueryWrapper<Item>().eq("itemContent", itemName));
        return ResultUtils.success(resItem.getId());
    }

    @GetMapping("/isRelation/{startItemId}/{endItemId}/{relationName}")
    public BaseResponse<Long> getItemIdByItemName(@PathVariable("startItemId") Long startItemId,
                                                  @PathVariable("endItemId") Long endItemId,
                                                  @PathVariable("relationName") String relationName,
                                                  HttpServletRequest request) {
        if (ObjectUtils.isEmpty(startItemId) || ObjectUtils.isEmpty(endItemId) || StringUtils.isBlank(relationName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelationType relationType = relationTypeService.getOne(new LambdaQueryWrapper<RelationType>()
                .eq(RelationType::getStartEntityItemId, startItemId)
                .eq(RelationType::getEndEntityItemId, endItemId)
                .eq(RelationType::getRelationName, relationName));
        return ResultUtils.success(relationType.getId());
    }

    public BaseResponse<Long> getItemIdsssssByItemName(@PathVariable("startItemId") Long startItemId,
                                                       @PathVariable("endItemId") Long endItemId,
                                                       @PathVariable("relationName") String relationName,
                                                       HttpServletRequest request) {
        if (ObjectUtils.isEmpty(startItemId) || ObjectUtils.isEmpty(endItemId) || StringUtils.isBlank(relationName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelationType relationType = relationTypeService.getOne(new LambdaQueryWrapper<RelationType>()
                .eq(RelationType::getStartEntityItemId, startItemId)
                .eq(RelationType::getEndEntityItemId, endItemId)
                .eq(RelationType::getRelationName, relationName));
        if (ObjectUtils.isEmpty(relationType)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        return ResultUtils.success(relationType.getId());
    }


    /**
     * 添加Cti情报接口
     *
     * @param ctiAddRequest
     * @param request
     * @return 返回的是CTI情报的ID
     */
    @PostMapping("/addCti")
    @Transactional
    public BaseResponse<Long> addCtiReport(@RequestBody CtiAddRequest ctiAddRequest, HttpServletRequest request) {
        if (ctiAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = new Cti();
        BeanUtils.copyProperties(ctiAddRequest, cti);
        ModelCtiVo modelCtiVo;

        try {
            // 获取到的对应的ctiId
            modelCtiVo = AiServer.getEntityAns(ctiAddRequest.getContent());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
        }


        Gson gson = new Gson();
        String jsonLabelListSting = gson.toJson(modelCtiVo.getLabelList());
        String jsonWordListSting = gson.toJson(modelCtiVo.getWordList());
        cti.setWordList(jsonWordListSting);
        cti.setLabelList(jsonLabelListSting);
        boolean result = ctiService.save(cti);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(cti.getId());
    }

    @PostMapping("/update/content")
    public BaseResponse<Long> updateCtiContentByCtiId(@RequestBody CtiUpdateRequest ctiUpdateRequest, HttpServletRequest request) {
        if (ctiUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = new Cti();
        BeanUtils.copyProperties(ctiUpdateRequest, cti);

        boolean result = ctiService.updateById(cti);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(cti.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> deleteCtiByCtiId(@RequestBody CtiDeleteRequest ctiDeleteRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(ctiDeleteRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = new Cti();
        BeanUtils.copyProperties(ctiDeleteRequest, cti);
        boolean result = ctiService.removeById(cti);
        boolean ctiChunkResult = ctiChunkService.remove(new LambdaQueryWrapper<CtiChunk>().eq(CtiChunk::getCtiId, cti.getId()));
        boolean entityResult = entityService.remove(new LambdaQueryWrapper<Entity>().eq(Entity::getCtiId, cti.getId()));
        boolean relationResult = relationService.remove(new LambdaQueryWrapper<Relation>().eq(Relation::getCtiId, cti.getId()));
//        ThrowUtils.throwIf(!(result && ctiChunkResult && entityResult && relationResult), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(cti.getId());
    }


    @PostMapping("/create/graph/{ctiId}")
    public BaseResponse<Boolean> pythonCreateGraph(@PathVariable("ctiId") Long ctiId) {
        if (ObjectUtils.isEmpty(ctiId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = ctiService.getById(ctiId);
        try {
            String s = new AiServer().pythonCreateGraph(cti);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
        }
        return ResultUtils.success(true);
    }


    @PostMapping("/add/relation/type")
    public BaseResponse<Long> addRelationType(@RequestBody RelationTypeAddRequest relationTypeAddRequest, HttpServletRequest request) {
        if (relationTypeAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelationType relationType = new RelationType();
        BeanUtils.copyProperties(relationTypeAddRequest, relationType);
        boolean result = relationTypeService.save(relationType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(relationType.getId());
    }


    /**
     * 添加关系类型
     *
     * @param ctiChunkAddRequest
     * @param request
     * @return
     */
    @PostMapping("/addCtiChunk")
    public BaseResponse addCtiChunk(@RequestBody CtiChunkAddRequest ctiChunkAddRequest, HttpServletRequest request) {
        if (ctiChunkAddRequest == null || ctiChunkAddRequest.getCtiChunkData().size() == 0) {
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

        return ResultUtils.error(ErrorCode.SUCCESS);
    }

    @PostMapping("/copy/model/data/{ctiId}")
    public BaseResponse<Boolean> copyModelData(@PathVariable("ctiId") Long ctiId) {
        if (ObjectUtils.isEmpty(ctiId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Cti cti = ctiService.getById(ctiId);
        try {
            String s = new AiServer().copyModelData(cti);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/home/view")
    public BaseResponse<HomeViewDataVo> getHomeViewData() {
        List<Item> sdoItemList = itemService.list(new LambdaQueryWrapper<Item>().eq(Item::getItemType, EntityTypeEnum.SDO.getValue()));
        Map<Long, Item> sdoIdMap = new HashMap<>();
        for (Item item : sdoItemList) {
            sdoIdMap.put(item.getId(), item);
        }
        List<Item> scoItemList = itemService.list(new LambdaQueryWrapper<Item>().eq(Item::getItemType, EntityTypeEnum.SCO.getValue()));
        Map<Long, Item> scoIdMap = new HashMap<>();
        for (Item item : scoItemList) {
            scoIdMap.put(item.getId(), item);
        }
        List<Long> sdoEntityIdList = sdoItemList.stream().map(Item::getId).collect(Collectors.toList());
        List<Long> scoEntityIdList = scoItemList.stream().map(Item::getId).collect(Collectors.toList());
        List<CtiChunk> sdoCtiChunkList = ctiChunkService.list(new LambdaQueryWrapper<CtiChunk>().in(CtiChunk::getItemId, sdoEntityIdList));
        List<CtiChunk> scoCtiChunkList = ctiChunkService.list(new LambdaQueryWrapper<CtiChunk>().in(CtiChunk::getItemId, scoEntityIdList));

        // 一个类型对应他有多少
        Map<Item, Integer> sdoResMap = new HashMap<>();
        for (CtiChunk ctiChunk : sdoCtiChunkList) {
            Item item = sdoIdMap.get(ctiChunk.getItemId());
            if (!sdoResMap.containsKey(item)) {
                sdoResMap.put(item, 1);
            } else {
                sdoResMap.put(item, sdoResMap.get(item) + 1);
            }
        }
        List<ItemHomeVo> sdoItemHomeVos = new ArrayList<>();
        for (Map.Entry<Item, Integer> itemIntegerEntry : sdoResMap.entrySet()) {
            ItemHomeVo itemHomeVo = new ItemHomeVo();
            Item item = itemIntegerEntry.getKey();
            BeanUtils.copyProperties(item, itemHomeVo);
            itemHomeVo.setItemDbCount(itemIntegerEntry.getValue());
            sdoItemHomeVos.add(itemHomeVo);
        }

        Map<Item, Integer> scoResMap = new HashMap<>();
        for (CtiChunk ctiChunk : scoCtiChunkList) {
            Item item = scoIdMap.get(ctiChunk.getItemId());
            if (!scoResMap.containsKey(item)) {
                scoResMap.put(item, 1);
            } else {
                scoResMap.put(item, scoResMap.get(item) + 1);
            }
        }
        List<ItemHomeVo> scoItemHomeVos = new ArrayList<>();
        for (Map.Entry<Item, Integer> itemIntegerEntry : scoResMap.entrySet()) {
            ItemHomeVo itemHomeVo = new ItemHomeVo();
            Item item = itemIntegerEntry.getKey();
            BeanUtils.copyProperties(item, itemHomeVo);
            itemHomeVo.setItemDbCount(itemIntegerEntry.getValue());
            scoItemHomeVos.add(itemHomeVo);
        }
        HomeViewDataVo homeViewDataVo = new HomeViewDataVo();
        homeViewDataVo.setSdoCount(sdoCtiChunkList.size());
        homeViewDataVo.setScoCount(scoCtiChunkList.size());
        homeViewDataVo.setCtiCount((int) ctiService.count());
        homeViewDataVo.setSdoItemHomeVos(sdoItemHomeVos);
        homeViewDataVo.setScoItemHomeVos(scoItemHomeVos);
        return ResultUtils.success(homeViewDataVo);
    }


    @GetMapping("/home/view/sdoItemsCount")
    public BaseResponse<HomeViewDataVo> getHomeSdoItemsCount() {
        HomeViewDataVo homeViewDataVo = new HomeViewDataVo();
        List<ItemHomeVo> sdoItemHomeVos = itemService.getHomeViewDiffListByItemType(EntityTypeEnum.SDO.getValue());
        homeViewDataVo.setSdoItemHomeVos(sdoItemHomeVos);
        homeViewDataVo.setSdoCount(sdoItemHomeVos.get(0).getTotalItemDbCount());
        return ResultUtils.success(homeViewDataVo);
    }

    @GetMapping("/home/view/scoItemsCount")
    public BaseResponse<HomeViewDataVo> getHomeScoItemsCount() {
        HomeViewDataVo homeViewDataVo = new HomeViewDataVo();
        List<ItemHomeVo> scoItemHomeVos = itemService.getHomeViewDiffListByItemType(EntityTypeEnum.SCO.getValue());
        homeViewDataVo.setScoItemHomeVos(scoItemHomeVos);
        homeViewDataVo.setScoCount(scoItemHomeVos.get(0).getTotalItemDbCount());
        return ResultUtils.success(homeViewDataVo);
    }

    @GetMapping("/home/view/ctiTotalCount")
    public BaseResponse<HomeViewDataVo> getHomeCtiTotalCount() {
        HomeViewDataVo homeViewDataVo = new HomeViewDataVo();
        homeViewDataVo.setCtiCount((int) ctiService.count());
        return ResultUtils.success(homeViewDataVo);
    }

    @PostMapping("/item/update")
    public BaseResponse<Boolean> updateItemData(@RequestBody ItemUpdateRequest itemUpdateRequest) {
        if (ObjectUtils.isEmpty(itemUpdateRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<Item> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Item::getId, itemUpdateRequest.getId()).set(Item::getBackgroundColor, itemUpdateRequest.getBackgroundColor());
        boolean result = itemService.update(updateWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

}
