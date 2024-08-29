package com.yxinmiracle.alsap.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yxinmiracle.alsap.model.dto.relationType.RelationTypeQueryRequest;
import com.yxinmiracle.alsap.model.entity.RelationType;
import com.yxinmiracle.alsap.model.vo.relationType.RelationTypeVo;

import java.util.List;

/**
 *
 */
public interface RelationTypeService extends IService<RelationType> {

    Page<RelationTypeVo> getRelationTypeVoListPage(RelationTypeQueryRequest relationTypeQueryRequest);
}
