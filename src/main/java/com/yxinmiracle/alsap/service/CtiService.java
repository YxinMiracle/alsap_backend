package com.yxinmiracle.alsap.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yxinmiracle.alsap.model.dto.graph.CtiNodeRelCtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.cti.CtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.cti.PreventEntityQuery;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.entity.Relation;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import com.yxinmiracle.alsap.model.vo.graph.GraphVo;
import com.yxinmiracle.alsap.model.vo.graph.NodeRelCtiVo;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface CtiService extends IService<Cti> {

    @ApiOperation(value = "根据Cti查询信息进行过滤")
    LambdaUpdateWrapper<Cti> getQueryWrapper(CtiQueryRequest ctiQueryRequest);

    List<Relation> getPreventGraphRelationDataList(PreventEntityQuery preventEntityQuery);

    @ApiOperation(value = "根据cti的id,获取这个cti中的sco有多少个,sdo有多少个")
    CtiVo getScoAndSdoCountByCtiId(Long ctiId, Map<Long, Item> itemId2ItemMap);

    @ApiOperation(value = "将Cti对象转为CtiVo对象，主要是多了关于这个cti的实体信息，比如sco多少，sdo是多少")
    Page<CtiVo> getCtiVOPage(Page<Cti> ctiPage, Map<Long, Item> itemId2ItemMap, HttpServletRequest request);

    @ApiOperation(value = "将ctiList转为，ctiVoList")
    List<CtiVo> getCtiVOList(List<Cti> ctiList, Map<Long, Item> itemId2ItemMap);

    @ApiOperation(value = "使用数据库语句查询数据")
    List<GraphVo> getGraphDataByCtiId(Long ctiId);

    @ApiOperation(value = "根据节点的名字和节点的ctiId搜索相关的情报信息")
    List<CtiVo> getNodeRelCtiData(CtiNodeRelCtiQueryRequest ctiNodeRelCtiQueryRequest);
}
