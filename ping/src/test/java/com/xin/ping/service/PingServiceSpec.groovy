package com.xin.ping.service

import com.xin.ping.utils.FileUtil
import org.slf4j.Logger
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject


@SpringBootTest
@TestPropertySource(properties = "scheduler.enabled=false")
class PingServiceSpec extends Specification {


    @Subject
    PingService pingService

    def setup() {
        // 初始化 PingService 并注入 Mock 的 WebClient
        pingService = new PingService()
        WebClient mockWebClient = Mock(WebClient)
        pingService.client = mockWebClient
        pingService.pongUrl = "http://localhost:8080/pong"

    }


    def "测试ping成功"() {
        given: "Mock FileUtil 和 Logger"
        GroovyMock(FileUtil, global: true)
        FileUtil.tryAcquire() >> true

        def mockLogger = Mock(Logger)
        pingService.log = mockLogger


        def actualResponse = Mono.just("world")

        def mockClient = Mock(WebClient.RequestHeadersUriSpec)
        def mockHeadersSpec = Mock(WebClient.RequestHeadersSpec)
        def mockResponseSpec = Mock(WebClient.ResponseSpec)

        // Mock WebClient 调用链
        pingService.client.get() >> mockClient
        mockClient.uri(pingService.pongUrl) >> mockHeadersSpec
        mockHeadersSpec.accept(_) >> mockHeadersSpec
        mockHeadersSpec.retrieve() >> mockResponseSpec
        mockResponseSpec.onStatus(_, _) >> mockResponseSpec
        mockResponseSpec.bodyToMono(String.class) >> actualResponse

        when: "调用 pingPongService 方法"
        pingService.pingPongService()

        then: "验证日志输出"
        1 * mockLogger.info("Request sent & Pong Respond: {}", "world")
    }

    def "测试ping失败"() {
        given: "Mock FileUtil 和 Logger"
        GroovyMock(FileUtil, global: true)
        FileUtil.tryAcquire() >> true

        def mockLogger = Mock(Logger)
        pingService.log = mockLogger


        def actualResponse = Mono.just("Too Many Requests")

        def mockClient = Mock(WebClient.RequestHeadersUriSpec)
        def mockHeadersSpec = Mock(WebClient.RequestHeadersSpec)
        def mockResponseSpec = Mock(WebClient.ResponseSpec)

        // Mock WebClient 调用链
        pingService.client.get() >> mockClient
        mockClient.uri(pingService.pongUrl) >> mockHeadersSpec
        mockHeadersSpec.accept(_) >> mockHeadersSpec
        mockHeadersSpec.retrieve() >> mockResponseSpec
        mockResponseSpec.onStatus(_, _) >> mockResponseSpec
        mockResponseSpec.bodyToMono(String.class) >> actualResponse

        when: "调用 pingPongService 方法"
        pingService.pingPongService()

        then: "验证日志输出"
        1 * mockLogger.info("Request sent & Pong Respond: {}", "Too Many Requests")
    }
}
