//package com.xin.pong.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//
//@SpringBootTest
//public class PongControllerTest {
//
//    @Autowired
//    private PongController pongController;
//
//
//    @Test
//    public void testPongFirstRequest() throws InterruptedException {
//        // 先休眠一秒防止同时测试报错
//        Thread.sleep(1000);
//
//        // 模拟首次请求
//        Mono<ResponseEntity<String>> response = pongController.pong();
//
//        // 验证响应状态为 200 OK，并且返回值为 "world"
//        StepVerifier.create(response)
//                .expectNextMatches(entity -> entity.getStatusCode() == HttpStatus.OK && "world".equals(entity.getBody()))
//                .verifyComplete();
//    }
//
//    @Test
//    public void testPongThrottledRequest() throws InterruptedException {
//        // 先休眠一秒防止同时测试报错
//        Thread.sleep(1000);
//
//        // 第一次请求，应该成功
//        pongController.pong().block();
//
//        // 第二次请求，在同一秒内，应该被限制
//        Mono<ResponseEntity<String>> throttledResponse = pongController.pong();
//
//        // 验证响应状态为 429 Too Many Requests，并且返回值为 "Too Many Requests"
//        StepVerifier.create(throttledResponse)
//                .expectNextMatches(entity -> entity.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS && "Too Many Requests".equals(entity.getBody()))
//                .verifyComplete();
//    }
//
//    @Test
//    public void testPongRequestAfterTimeElapsed() throws InterruptedException {
//        // 先休眠一秒防止同时测试报错
//        Thread.sleep(1000);
//
//        // 第一次请求，应该成功
//        pongController.pong().block();
//
//        // 模拟时间流逝（超过1秒），下一次请求应当成功
//        Thread.sleep(1000);
//
//        Mono<ResponseEntity<String>> responseAfterTimeElapsed = pongController.pong();
//
//        // 验证响应状态为 200 OK，并且返回值为 "world"
//        StepVerifier.create(responseAfterTimeElapsed)
//                .expectNextMatches(entity -> entity.getStatusCode() == HttpStatus.OK && "world".equals(entity.getBody()))
//                .verifyComplete();
//    }
//}
