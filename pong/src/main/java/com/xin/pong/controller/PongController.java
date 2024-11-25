package com.xin.pong.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PongController {

    private static final Logger log = LoggerFactory.getLogger(PongController.class);
    private final AtomicLong lastRequestTimestamp = new AtomicLong(0);

    @GetMapping(value = "/pong")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> pong() {
        long currentSecond = Instant.now().getEpochSecond();
        if (lastRequestTimestamp.get() != currentSecond) {
            lastRequestTimestamp.set(currentSecond);
            log.info("Request Success!");
            return Mono.just(ResponseEntity.ok("world"));
        } else {
            log.info("Request throttled!");
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too Many Requests"));
        }
    }
}
