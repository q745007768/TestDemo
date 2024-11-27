package com.xin.pong.controller


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.Duration

@SpringBootTest
class PongControllerTest extends Specification {


    @Autowired
    PongController pongController;

    def "TestPongSuccess"() {
        when:
        Mono response = pongController.pong("Hello")

        then:
        StepVerifier.create(response)
                .expectNextMatches { it.statusCode == HttpStatus.OK && it.body == "world" }
                .verifyComplete()
    }

    def "TestTooManyRequests"() {
        given:
        Mono.delay(Duration.ofSeconds(1)).block()

        pongController.pong("Hello").block()

        when:
        Mono response = pongController.pong("Hello")

        then:
        StepVerifier.create(response)
                .expectNextMatches { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                .verifyComplete()
    }
}