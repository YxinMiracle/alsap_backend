package com.yxinmiracle.alsap.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yxinmiracle.alsap.model.dto.cti.CtiAddRequest;
import com.yxinmiracle.alsap.model.dto.cti.CtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.cti.PreventEntityQuery;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yxinmiracle.alsap.model.entity.Relation;

import java.util.List;

/**
 *
 */
public interface CtiService extends IService<Cti> {

    LambdaUpdateWrapper<Cti> getQueryWrapper(CtiQueryRequest ctiQueryRequest);

    List<Relation> getPreventGraphRelationDataList(PreventEntityQuery preventEntityQuery);

}
