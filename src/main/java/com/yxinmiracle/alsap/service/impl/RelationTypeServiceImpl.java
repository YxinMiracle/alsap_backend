package com.yxinmiracle.alsap.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.mapper.RelationTypeMapper;
import com.yxinmiracle.alsap.model.dto.relationType.RelationTypeQueryRequest;
import com.yxinmiracle.alsap.model.entity.RelationType;
import com.yxinmiracle.alsap.model.vo.relationType.RelationTypeVo;
import com.yxinmiracle.alsap.service.RelationTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Service
public class RelationTypeServiceImpl extends ServiceImpl<RelationTypeMapper, RelationType>
    implements RelationTypeService{

    @Resource
    private RelationTypeMapper relationTypeMapper;

    @Override
    public Page<RelationTypeVo>  getRelationTypeVoListPage(RelationTypeQueryRequest relationTypeQueryRequest) {
        // 获取当前页数以及对应的请求大小
        long current = relationTypeQueryRequest.getCurrent();
        long size = relationTypeQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        int offset = (relationTypeQueryRequest.getCurrent() - 1) * relationTypeQueryRequest.getPageSize();
        relationTypeQueryRequest.setOffset(offset);


        Long totalCount = relationTypeMapper.getTotalCount(relationTypeQueryRequest);
        List<RelationTypeVo> relationTypeVoList = relationTypeMapper.getRelationTypeVoListPage(relationTypeQueryRequest);

        Page<RelationTypeVo> relationTypeVoPage = new Page<>(current, size,totalCount);
        relationTypeVoPage.setRecords(relationTypeVoList);
        return relationTypeVoPage;
    }
}




