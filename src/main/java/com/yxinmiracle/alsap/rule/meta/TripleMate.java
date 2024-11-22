package com.yxinmiracle.alsap.rule.meta;

/*
 * @author  YxinMiracle
 * @date  2024-10-31 4:09
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TripleMate {


    private SubjectData subject;
    private String relation;
    private SubjectData object;

    @NoArgsConstructor
    @Data
    public static class SubjectData {
        private String name;
        private String type;
    }

}
