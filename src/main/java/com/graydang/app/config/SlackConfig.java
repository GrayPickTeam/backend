package com.graydang.app.config;

import com.graydang.app.monitoring.AsyncSlackBotNotifier;
import com.graydang.app.monitoring.SlackBotNotifier;
import com.graydang.app.monitoring.SlackNotifier;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
class SlackConfig {
  @Value("${slack.bot.token:${SLACK_BOT_TOKEN:}}")
  private String SLACK_BOT_TOKEN;

  @Bean
  WebClient slackWebClient() {
    var strategies = ExchangeStrategies.builder()
        .codecs(c -> c.defaultCodecs().maxInMemorySize(256 * 1024))
        .build();

    HttpClient http = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
        .responseTimeout(Duration.ofSeconds(5));

    return WebClient.builder()
        .baseUrl("https://slack.com/api") // ✅ 중요
        .clientConnector(new ReactorClientHttpConnector(http))
        .exchangeStrategies(strategies)
        .defaultHeaders(h -> {
          h.setBearerAuth(SLACK_BOT_TOKEN);
          h.setAccept(List.of(MediaType.APPLICATION_JSON));
          h.set("Content-Type", "application/json; charset=utf-8");
        })
        .build();
  }

  @Bean
  SlackNotifier slackNotifier(WebClient slackWebClient) {
    return new SlackBotNotifier(slackWebClient);
  }

  @Bean
  SlackNotifier asyncSlackNotifier(WebClient slackWebClient) {
    return new AsyncSlackBotNotifier(slackWebClient);
  }
}
