package com.yxinmiracle.alsap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 方法级注解
@Retention(RetentionPolicy.RUNTIME) // 运行时可见
public @interface DecryptRequestBody {
}
