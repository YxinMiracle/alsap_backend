package com.yxinmiracle.alsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxinmiracle.alsap.model.dto.relationType.RelationTypeQueryRequest;
import com.yxinmiracle.alsap.model.entity.RelationType;
import com.yxinmiracle.alsap.model.vo.relationType.RelationTypeVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.yxinmiracle.alsap.model.entity.RelationType
 */
public interface RelationTypeMapper extends BaseMapper<RelationType> {

    List<RelationTypeVo> getRelationTypeVoListPage(RelationTypeQueryRequest relationTypeQueryRequest);

    Long getTotalCount(RelationTypeQueryRequest relationTypeQueryRequest);

    @Select("SELECT DISTINCT relationName from relation_type")
    List<String> getRelationTypeNameList();
}




