package com.yxinmiracle.alsap.aop;


import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxinmiracle.alsap.annotation.IpFlowLimit;
import com.yxinmiracle.alsap.common.ErrorCode;
import com.yxinmiracle.alsap.common.ResultUtils;
import com.yxinmiracle.alsap.model.entity.Cti;
import com.yxinmiracle.alsap.model.entity.Item;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Aspect
@Component
public class IpFlowLimitAspect {

    private Set<String> processedMethods = new HashSet<>();


    // 创建限流规则并加载
    private void createAndLoadFlowRules(IpFlowLimit ipFlowLimit, String className) {
        // 创建限流规则
        ParamFlowRule rule = new ParamFlowRule(className)  // 使用类名作为资源名称
                .setParamIdx(0)  // 限流基于第 0 个参数（即 IP 地址）
                .setCount(ipFlowLimit.maxRequests())  // 最大请求数
                .setDurationInSec(ipFlowLimit.timeWindow());  // 时间窗口

        // 获取当前已加载的规则
        List<ParamFlowRule> existingRules = ParamFlowRuleManager.getRules();

        // 如果已经有规则，则将新的规则添加到现有规则中
        if (existingRules == null) {
            existingRules = new ArrayList<>();
        }
        existingRules.add(rule);

        // 加载所有规则
        ParamFlowRuleManager.loadRules(existingRules);
    }


    @Around("@annotation(ipFlowLimit)")  // 方法执行前执行限流规则配置
    public Object handleIpFlowLimit(ProceedingJoinPoint joinPoint, IpFlowLimit ipFlowLimit) throws Exception {
        String resourceName = ipFlowLimit.resourceName();  // 获取注解中的资源名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 获取 IP 地址
        String remoteAddr = request.getRemoteAddr();

        // 判断当前类是否已经处理过，如果没有处理过，则创建限流规则
        if (!processedMethods.contains(resourceName)) {
            // 如果是第一次遇到该类名，创建限流规则
            createAndLoadFlowRules(ipFlowLimit, resourceName);
            // 将类名添加到已处理集合中，防止重复加载
            processedMethods.add(resourceName);
        }


        Entry entry = null;
        try {
            entry = SphU.entry(resourceName, EntryType.IN, 1, remoteAddr);
            // 获取方法的参数
            Object[] args = joinPoint.getArgs();
            return joinPoint.proceed(args);
        } catch (Throwable ex){
            // 自定义业务异常
            if (!BlockException.isBlockException(ex)){
                Tracer.trace(ex);
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
            }
            if (ex instanceof DegradeException) {
                return  ResultUtils.success(null);
            }
            // 限流操作
            return ResultUtils.error(ErrorCode.USER_HEIGHT_PRESSURE, "用户频繁访问，您已被暂时封禁");
        } finally {
            if (entry != null) {
                entry.exit(1, remoteAddr);
            }
        }
    }


}
