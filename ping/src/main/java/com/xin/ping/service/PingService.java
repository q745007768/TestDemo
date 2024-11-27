package com.xin.ping.service;
import com.xin.ping.utils.FileUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Component
public class PingService {
    public Logger log = LoggerFactory.getLogger(PingService.class);

    @Value("${pongUrl}")
    public String pongUrl;

    private final FileUtil fileUtil;

    public WebClient client;

    public PingService(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @PostConstruct
    public void init() {
        client = WebClient.create();
    }

    public void pingPongService() {
        if (fileUtil.tryAcquire()) {
            Mono<String> response = client.post()
                    .uri(pongUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue("Hello")
                    .retrieve()
                    .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, clientResponse -> Mono.empty())
                    .bodyToMono(String.class);

            response.subscribe(
                    result -> log.info("Request sent & Pong Respond: {}", result),
                    error -> log.info("Request not sent due to being 'rate limited' or other error: {}", error.getMessage())
            );
        }
    }
}
