package com.xin.ping.service;
import com.xin.ping.utils.FileUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class PingService {
    public Logger log = LoggerFactory.getLogger(PingService.class);

    @Value("${pongUrl}")
    public String pongUrl;

    public WebClient client;

    @PostConstruct
    public void init() {
        client = WebClient.create();
    }

    public void pingPongService() {
        if (FileUtil.tryAcquire()) {
            Mono<String> response = client.get()
                    .uri(pongUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, clientResponse -> Mono.empty())
                    .bodyToMono(String.class);

            response.subscribe(result -> {
                log.info("Request sent & Pong Respond: {}", result);
            }, error -> log.info("Request not sent due to being 'rate limited' or other error: {}", error.getMessage()));
        }
    }
}
