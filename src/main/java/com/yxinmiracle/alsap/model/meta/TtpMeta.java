package com.yxinmiracle.alsap.model.meta;

/*
 * @author  YxinMiracle
 * @date  2024-09-23 13:59
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
public class TtpMeta implements Serializable {

    // 类型名称
    private String name;

    // 工具版本呢信息
    private VersionsConfig versions;

    @NoArgsConstructor
    @Data
    public static class VersionsConfig {
        private String attack;
        private String navigator;
        private String layer;
    }

    private String domain;

    private String description;

    private FiltersConfig filters;

    @NoArgsConstructor
    @Data
    public static class FiltersConfig {
        private List<String> platforms;
    }

    // 不知道是干嘛的，先占个位置
    private Integer sorting;

    private LayoutConfig layout;

    @NoArgsConstructor
    @Data
    public static class LayoutConfig {
        private String layout;
        private String aggregateFunction;
        private Boolean showID;
        private Boolean showName;
        private Boolean showAggregateScores;
        private Boolean countUnscored;
        private String expandedSubtechniques;
    }

    private Boolean hideDisabled;

    private List<TechniquesConfig> techniques;

    @NoArgsConstructor
    @Data
    public static class TechniquesConfig {
        private String techniqueID;
        private String tactic;
        private String color;
        private String comment;
        private Boolean enabled;
        private List<String> metadata;
        private List<String> links;
        private Boolean showSubtechniques;
    }

    private GradientConfig gradient;

    @NoArgsConstructor
    @Data
    public static class GradientConfig {
        private List<String> colors;
        private Integer minValue;
        private Integer maxValue;
    }

    private List<String> legendItems;

    private List<String> metadata;

    private List<String> links;

    private Boolean showTacticRowBackground;

    private String tacticRowBackground;

    private Boolean selectTechniquesAcrossTactics;

    private Boolean selectSubtechniquesWithParent;

    private Boolean selectVisibleTechniques;
}
