package com.yxinmiracle.alsap.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.mapper.ItemMapper;
import com.yxinmiracle.alsap.mapper.RelationTypeMapper;
import com.yxinmiracle.alsap.model.dto.relationType.RelationTypeQueryRequest;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.entity.RelationType;
import com.yxinmiracle.alsap.model.vo.relationType.RelationTypeVo;
import com.yxinmiracle.alsap.service.RelationTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class RelationTypeServiceImpl extends ServiceImpl<RelationTypeMapper, RelationType>
        implements RelationTypeService {

    @Resource
    private RelationTypeMapper relationTypeMapper;

    @Resource
    private ItemMapper itemMapper;

    @Override
    public Page<RelationTypeVo> getRelationTypeVoListPage(RelationTypeQueryRequest relationTypeQueryRequest) {
        // 获取当前页数以及对应的请求大小
        long current = relationTypeQueryRequest.getCurrent();
        long size = relationTypeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        int offset = (relationTypeQueryRequest.getCurrent() - 1) * relationTypeQueryRequest.getPageSize();
        relationTypeQueryRequest.setOffset(offset);


        Long totalCount = relationTypeMapper.getTotalCount(relationTypeQueryRequest);
        List<RelationTypeVo> relationTypeVoList = relationTypeMapper.getRelationTypeVoListPage(relationTypeQueryRequest);

        Page<RelationTypeVo> relationTypeVoPage = new Page<>(current, size, totalCount);
        relationTypeVoPage.setRecords(relationTypeVoList);
        return relationTypeVoPage;
    }

    @Override
    public List<String> getRelationTypeNameList() {
        List<String> relationTypeNameList = relationTypeMapper.getRelationTypeNameList();
        if (CollUtil.isEmpty(relationTypeNameList)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return relationTypeNameList;
    }

    @Override
    public void validRelationType(RelationType relationType) {
        if (relationType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Long> itemIdList = itemMapper.selectList(null).stream().map(Item::getId).collect(Collectors.toList());

        if (!itemIdList.contains(relationType.getStartEntityItemId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新的实体并不属于系统");
        }

        if (!itemIdList.contains(relationType.getEndEntityItemId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新的实体并不属于系统");
        }

        Set<String> relationTypeNameSet = relationTypeMapper.selectList(null).stream().map(RelationType::getRelationName).collect(Collectors.toSet());

        if (!relationTypeNameSet.contains(relationType.getRelationName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新的关系并不属于系统");
        }
    }
}




