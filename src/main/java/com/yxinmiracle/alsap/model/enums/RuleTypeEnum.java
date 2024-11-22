package com.yxinmiracle.alsap.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 规则更新策略
 */
public enum RuleTypeEnum {

    YARA("yara", 1),
    SNORT("snort", 2);

    private final String text;

    private final Integer value;

    RuleTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 text 获取枚举
     *
     * @param text
     * @return
     */
    public static RuleTypeEnum getEnumByText(String text) {
        if (ObjectUtils.isEmpty(text)) {
            return null;
        }
        for (RuleTypeEnum anEnum : RuleTypeEnum.values()) {
            if (anEnum.text.equals(text)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static RuleTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (RuleTypeEnum anEnum : RuleTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

}

