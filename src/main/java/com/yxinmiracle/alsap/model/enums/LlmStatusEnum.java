package com.yxinmiracle.alsap.model.enums;

/**
 * 大模型生成结果状态
 */
public enum LlmStatusEnum {

    NOT_REQUESTED("NOT_REQUESTED", 1),
    REQUESTED("REQUESTED", 2),
    RESPONSE_ERROR("RESPONSE_ERROR", 4),
    RESPONSE_RECEIVED("RESPONSE_RECEIVED", 3);

    private final String text;

    private final Integer value;

    LlmStatusEnum(String text, Integer value) {
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

