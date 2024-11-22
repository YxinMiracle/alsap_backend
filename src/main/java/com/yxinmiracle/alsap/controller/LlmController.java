package com.yxinmiracle.alsap.controller;

/*
 * @author  YxinMiracle
 * @date  2024-10-30 12:40
 * @Gitee: https://gitee.com/yxinmiracle
 */

import com.yxinmiracle.alsap.model.dto.llm.LlmChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/llm")
@Slf4j
public class LlmController {

    @Value("${aiServer.authHeader}")
    private String AUTH_HEADER="alsapYxinMiracleAuthKey";

    @Value("${aiServer.authSecret}")
    private String AUTH_REQUEST_SECRET="alsapYxinMiracleSecretKeyYep";

    @Value("${aiServer.host}")
//    public String AI_SERVER_URL = "http://172.23.207.70:6000/";
    public String AI_SERVER_URL = "http://alsap_frp.tracesec.cn:8365/";

    private final WebClient webClient = WebClient.create(AI_SERVER_URL);

    /**
     * 用于获取大模型的回复
     *
     * @param llmChatRequest
     * @return
     */
    @GetMapping(value = "/forward", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getLlmAnsByStream(@ModelAttribute LlmChatRequest llmChatRequest) {
        String uri = UriComponentsBuilder.fromUriString("/rule/chat")
                .queryParam("systemPrompts", llmChatRequest.getSystemPrompts())
                .queryParam("userQuestion", llmChatRequest.getUserQuestion())
                .build().toUriString();

        return webClient.get()
                .uri(uri)
                .header(AUTH_HEADER, AUTH_REQUEST_SECRET)
                .retrieve()
                .bodyToFlux(String.class);
    }

}
