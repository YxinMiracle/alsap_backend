package com.yxinmiracle.alsap.model.enums;

/**
 * ttp识别业务类型枚举
 */
public enum TtpStatusEnum {


    IDLE("默认状态", 0),
    PROCESSING("处理中", 1),
    COMPLETED("已完成", 2),
    Retrying("重试中", 3);

    private final String text;

    private final Integer value;

    TtpStatusEnum(String text, Integer value) {
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
        System.out.println(TtpStatusEnum.PROCESSING.getValue());
    }
}
