package com.yxinmiracle.alsap.model.enums;

/**
 * 用户角色枚举
 */
public enum ItemTypeEnum {

    SDO("域对象", 1),
    SCO("可观测对象", 2);

    private final String text;

    private final Integer value;

    ItemTypeEnum(String text, Integer value) {
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
        System.out.println(ItemTypeEnum.SCO.getValue());
    }
}

