package com.yxinmiracle.alsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.CtiAddRequest;
import com.yxinmiracle.alsap.model.dto.cti.CtiQueryRequest;
import com.yxinmiracle.alsap.model.dto.cti.PreventEntityQuery;
import com.yxinmiracle.alsap.model.dto.model.ModelCtiVo;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.Relation;
import com.yxinmiracle.alsap.service.CtiService;
import com.yxinmiracle.alsap.mapper.CtiMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
public class CtiServiceImpl extends ServiceImpl<CtiMapper, Cti>
    implements CtiService{

    @Resource
    private CtiMapper ctiMapper;

    @Override
    public LambdaUpdateWrapper<Cti> getQueryWrapper(CtiQueryRequest ctiQueryRequest) {
        LambdaUpdateWrapper<Cti> ctiQueryWrapper = new LambdaUpdateWrapper<>();
        if (ctiQueryRequest == null){
            return ctiQueryWrapper;
        }
        String title = ctiQueryRequest.getTitle();
        Long userId = ctiQueryRequest.getUserId();
        Date createTime = ctiQueryRequest.getCreateTime();
        Date updateTime = ctiQueryRequest.getUpdateTime();

        // 拼接查询条件
        if (StringUtils.isNotBlank(title)) {
            ctiQueryWrapper.like(StringUtils.isNotBlank(title),Cti::getTitle,title);
        }
        ctiQueryWrapper.orderByDesc(Cti::getUpdateTime);
        ctiQueryWrapper.eq(ObjectUtils.isNotEmpty(userId), Cti::getUserId, userId);
        ctiQueryWrapper.ge(ObjectUtils.isNotEmpty(createTime), Cti::getCreateTime, createTime);
        ctiQueryWrapper.ge(ObjectUtils.isNotEmpty(updateTime), Cti::getUpdateTime, updateTime);
        return ctiQueryWrapper;
    }

    @Override
    public List<Relation> getPreventGraphRelationDataList(PreventEntityQuery preventEntityQuery) {
        List<Relation> relationList = ctiMapper.preventEntitySearch(preventEntityQuery);
        return relationList;
    }

}




