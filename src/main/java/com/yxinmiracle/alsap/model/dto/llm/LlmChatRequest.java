package com.yxinmiracle.alsap.model.dto.llm;

/*
 * @author  YxinMiracle
 * @date  2024-11-11 15:07
 * @Gitee: https://gitee.com/yxinmiracle
 */

import lombok.Data;

@Data
public class LlmChatRequest {

    private String systemPrompts;
    private String userQuestion;


}
