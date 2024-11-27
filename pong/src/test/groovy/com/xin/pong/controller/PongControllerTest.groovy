package com.xin.pong.controller


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

@SpringBootTest
class PongControllerTest extends Specification {


    @Autowired
    PongController pongController;

    def "第一个请求应在请求中返回200"() {
        when:
        Mono response = pongController.pong()

        then:
        StepVerifier.create(response)
                .expectNextMatches { it.statusCode == HttpStatus.OK && it.body == "world" }
                .verifyComplete()
    }

    def "对于同一秒内的后续请求，应返回429请求过多"() {
        given:
        pongController.pong().block()

        when:
        Mono response = pongController.pong()

        then:
        StepVerifier.create(response)
                .expectNextMatches { it.statusCode == HttpStatus.TOO_MANY_REQUESTS }
                .verifyComplete()
    }
}