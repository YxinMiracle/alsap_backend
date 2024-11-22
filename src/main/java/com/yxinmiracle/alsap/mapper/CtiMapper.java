package com.yxinmiracle.alsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxinmiracle.alsap.model.dto.graph.CtiNodeRelCtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.cti.PreventEntityQuery;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.Relation;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import com.yxinmiracle.alsap.model.vo.graph.GraphVo;
import com.yxinmiracle.alsap.model.vo.graph.NodeRelCtiVo;

import java.util.List;

/**
 * @Entity com.yxinmiracle.alsap.model.entity.Cti
 */
public interface CtiMapper extends BaseMapper<Cti> {

    List<Relation> preventEntitySearch(PreventEntityQuery preventEntityQuery);

    List<GraphVo> getGraphDataByCtiId(Long ctiId);

    List<CtiVo> getNodeRelCtiData(CtiNodeRelCtiQueryRequest ctiNodeRelCtiQueryRequest);
}




