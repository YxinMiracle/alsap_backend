package com.yxinmiracle.alsap.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 */
public enum EntityTypeEnum {

    SDO("域对象", 1),
    SCO("可观测对象", 2);

    private final String text;

    private final Integer value;

    EntityTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }


    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static void main(String[] args) {
        System.out.println(EntityTypeEnum.SCO.getValue());
    }
}

