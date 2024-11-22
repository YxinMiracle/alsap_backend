package com.yxinmiracle.alsap.model.enums;

/**
 * 规则文件生成状态
 */
public enum RuleFileStatusEnum {

    NOT_GENERATED("NOT_GENERATED", 1),
    GENERATED("GENERATED", 2);

    private final String text;

    private final Integer value;

    RuleFileStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }


    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

}

