package com.yxinmiracle.alsap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // 目标为方法
@Retention(RetentionPolicy.RUNTIME)  // 在运行时生效
public @interface IpFlowLimit {
    String resourceName();  // 限流资源名
    int maxRequests() default 8;  // 默认每分钟最多请求次数
    int timeWindow() default 100;  // 默认时间窗口 60 秒
}
