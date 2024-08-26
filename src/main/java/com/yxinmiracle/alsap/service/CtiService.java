package com.yxinmiracle.alsap.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yxinmiracle.alsap.model.dto.cti.CtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.cti.PreventEntityQuery;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.Item;
import com.yxinmiracle.alsap.model.entity.Relation;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
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

    @ApiOperation(value = "将Cti对象转为CtiVo对象，主要是多了关于这个cti的实体信息，比如sco多少，sdo是多少")
    Page<CtiVo> getCtiVOPage(Page<Cti> ctiPage, Map<Long, Item> itemId2ItemMap, HttpServletRequest request);
}
