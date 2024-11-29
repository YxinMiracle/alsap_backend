package com.yxinmiracle.alsap.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yxinmiracle.alsap.aiService.AiServer;
import com.yxinmiracle.alsap.annotation.AuthCheck;
import com.yxinmiracle.alsap.annotation.DecryptRequestBody;
import com.yxinmiracle.alsap.common.AiServerRet;
import com.yxinmiracle.alsap.common.BaseResponse;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.constant.UserConstant;
import com.yxinmiracle.alsap.exception.BusinessException;
import com.yxinmiracle.alsap.exception.ThrowUtils;
import com.yxinmiracle.alsap.model.dto.cti.*;
import com.yxinmiracle.alsap.model.dto.wx.WxAccessTokenDto;
import com.yxinmiracle.alsap.model.dto.wx.WxTicketDto;
import com.yxinmiracle.alsap.model.entity.*;
import com.yxinmiracle.alsap.model.enums.TtpStatusEnum;
import com.yxinmiracle.alsap.model.vo.cti.CtiDetailVo;
import com.yxinmiracle.alsap.model.vo.cti.CtiTtpExtractVo;
import com.yxinmiracle.alsap.model.vo.cti.CtiVo;
import com.yxinmiracle.alsap.model.vo.cti.CtiWordLabelVo;
import com.yxinmiracle.alsap.model.vo.user.NoRoleUserVo;
import com.yxinmiracle.alsap.service.*;
import com.yxinmiracle.alsap.utils.RequestUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * cti接口
 */
@RestController
@RequestMapping("/wx")
@Slf4j
public class WechatController {

    @Value("${open.appId}")
    private String appid;


    @Value("${open.appSecret}")
    private String secret;

    private String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    private String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    /**
     * 获取access_token
     *
     * @return
     */
    @GetMapping("/ticket")
    public BaseResponse<String> getTicket() {
        // 获得 accessToken
        String getAccessTokenUrl = String.format(accessTokenUrl, appid, secret);
        System.out.println(">>>>>>>>>>>"+getAccessTokenUrl);
        HttpResponse httpResponse = RequestUtils.getWithNoHeader(getAccessTokenUrl);
        String access_token = httpResponse.body();
        WxAccessTokenDto wxAccessTokenDto = JSONUtil.toBean(access_token, WxAccessTokenDto.class);
        String accessToken = wxAccessTokenDto.getAccess_token();
        System.out.println(">>>>>>>>>>>"+ accessToken);
        // 获得jsapiTicket
        String getJsapiTicketUrl = String.format(jsapiTicketUrl, accessToken);
        HttpResponse ticketResponse = RequestUtils.getWithNoHeader(getJsapiTicketUrl);
        String ticketJsonResponse = ticketResponse.body();
        WxTicketDto wxTicketDto = JSONUtil.toBean(ticketJsonResponse, WxTicketDto.class);
        String ticket = wxTicketDto.getTicket();
        System.out.println(">>>>>>>>>>>"+ ticket);
        return ResultUtils.success(ticket);
    }


}
