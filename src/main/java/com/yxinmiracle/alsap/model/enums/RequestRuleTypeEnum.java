package com.yxinmiracle.alsap.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 规则更新策略
 */
public enum RequestRuleTypeEnum {

    CREATE_RULE("创建规则", 1),
    UPDATE_RULE("更新对象", 2);

    private final String text;

    private final Integer value;

    RequestRuleTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static RequestRuleTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (RequestRuleTypeEnum anEnum : RequestRuleTypeEnum.values()) {
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

