package com.yxinmiracle.alsap.model.meta;

/*
 * @author  YxinMiracle
 * @date  2024-09-23 13:59
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
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

        public static class Builder {
            private String techniqueID;
            private String tactic;
            private String color = "#fc3b3b";
            private String comment = "";
            private Boolean enabled = true;
            private List<String> metadata = new ArrayList<>();
            private List<String> links = new ArrayList<>();
            private Boolean showSubtechniques = false;

            public Builder techniqueID(String techniqueID) {
                this.techniqueID = techniqueID;
                return this;
            }

            public Builder tactic(String tactic) {
                this.tactic = tactic;
                return this;
            }

            public Builder color(String color) {
                this.color = color;
                return this;
            }

            public Builder comment(String comment) {
                this.comment = comment;
                return this;
            }

            public Builder enabled(Boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public Builder metadata(List<String> metadata) {
                this.metadata = metadata;
                return this;
            }

            public Builder links(List<String> links) {
                this.links = links;
                return this;
            }

            public Builder showSubtechniques(Boolean showSubtechniques) {
                this.showSubtechniques = showSubtechniques;
                return this;
            }

            public TechniquesConfig build() {
                return new TechniquesConfig(techniqueID, tactic, color, comment, enabled, metadata, links, showSubtechniques);
            }
        }

        private TechniquesConfig(String techniqueID, String tactic, String color, String comment,
                                 Boolean enabled, List<String> metadata, List<String> links, Boolean showSubtechniques) {
            this.techniqueID = techniqueID;
            this.tactic = tactic;
            this.color = color;
            this.comment = comment;
            this.enabled = enabled;
            this.metadata = metadata;
            this.links = links;
            this.showSubtechniques = showSubtechniques;
        }
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
