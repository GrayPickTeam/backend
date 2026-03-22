package com.graydang.app.monitoring;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class AsyncSlackBotNotifier implements SlackNotifier {

    private final WebClient client;
    private static final String CHANNEL_ID = "monitoring";

    public AsyncSlackBotNotifier(WebClient client) {
        this.client = client;
    }

    @Override
    public void send(SlackPayload payload) {
        sendToChannel(CHANNEL_ID, payload);
    }

    @Override
    public void send(String channel, SlackPayload payload) {
        String targetChannel = (channel != null && !channel.isBlank()) ? channel : CHANNEL_ID;
        sendToChannel(targetChannel, payload);
    }

    private void sendToChannel(String channel, SlackPayload payload) {
        Map<String, Object> body = new HashMap<>();
        body.put("channel", channel);
        if (payload.text() != null && !payload.text().isBlank()) {
            body.put("text", payload.text());
        }

        client.post()
            .uri("/chat.postMessage")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .onStatus(s -> s.value() == 429, resp -> resp.createException())
            .bodyToMono(String.class)
            .subscribe(
                res -> log.debug("[Slack] 전송 성공: {}", res),
                err -> log.error("[Slack] 전송 실패: {}", err.getMessage())
            );
    }
}
