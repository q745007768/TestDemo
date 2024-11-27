package com.xin.ping.service

import com.xin.ping.utils.FileUtil
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration
import java.util.concurrent.TimeoutException


@SpringBootTest
@TestPropertySource(properties = "scheduler.enabled=false")
class PingServiceTest extends Specification {
    FileUtil fileUtil

    @Subject
    PingService pingService

    def setup() {
        // 手动初始化 FileUtil
        fileUtil = new FileUtil()
        fileUtil.LOCK_FILE_PATH = "D://rate_limit.txt"
        // 初始化 PingService 并注入 Mock 的 WebClient
        pingService = new PingService(fileUtil)
        WebClient mockWebClient = Mock(WebClient)
        pingService.client = mockWebClient
        pingService.pongUrl = "http://localhost:8080/pong"

    }


    def "TestPingSuccess"() {
        given: "Mock FileUtil 和 Logger"
        GroovyMock(FileUtil, global: true)
        fileUtil.tryAcquire() >> true

        def mockLogger = Mock(Logger)
        pingService.log = mockLogger

        def actualResponse = Mono.just("world")

        def mockClient = Mock(WebClient.RequestBodyUriSpec)
        def mockHeadersSpec = Mock(WebClient.RequestBodySpec)
        def mockResponseSpec = Mock(WebClient.ResponseSpec)

        // Mock WebClient 调用链
        pingService.client.post() >> mockClient
        mockClient.uri(pingService.pongUrl) >> mockHeadersSpec
        mockHeadersSpec.accept(_) >> mockHeadersSpec
        mockHeadersSpec.bodyValue("Hello") >> mockHeadersSpec  // 设置请求体为 "Hello"
        mockHeadersSpec.retrieve() >> mockResponseSpec
        mockResponseSpec.onStatus(_, _) >> mockResponseSpec
        mockResponseSpec.bodyToMono(String.class) >> actualResponse

        when: "调用 pingPongService 方法"
        pingService.pingPongService()

        then: "验证日志输出"
        1 * mockLogger.info("Request sent & Pong Respond: {}", "world")
    }

    def "TestPingThrottled"() {
        given: "Mock FileUtil 和 Logger"
        GroovyMock(FileUtil, global: true)
        fileUtil.tryAcquire() >> true

        def mockLogger = Mock(Logger)
        pingService.log = mockLogger

        def actualResponse = Mono.just("Too Many Requests")

        def mockClient = Mock(WebClient.RequestBodyUriSpec)
        def mockHeadersSpec = Mock(WebClient.RequestBodySpec)
        def mockResponseSpec = Mock(WebClient.ResponseSpec)

        // Mock WebClient 调用链
        pingService.client.post() >> mockClient
        mockClient.uri(pingService.pongUrl) >> mockHeadersSpec
        mockHeadersSpec.accept(_) >> mockHeadersSpec
        mockHeadersSpec.bodyValue("Hello") >> mockHeadersSpec  // 设置请求体为 "Hello"
        mockHeadersSpec.retrieve() >> mockResponseSpec
        mockResponseSpec.onStatus(_, _) >> mockResponseSpec
        mockResponseSpec.bodyToMono(String.class) >> actualResponse

        when: "调用 pingPongService 方法"
        pingService.pingPongService()

        then: "验证日志输出"
        1 * mockLogger.info("Request sent & Pong Respond: {}", "Too Many Requests")
    }

    def "testPingError"() {
        given: "Mock FileUtil 和 WebClient 返回错误响应"
        GroovyMock(FileUtil, global: true)
        fileUtil.tryAcquire() >> true
        Mono.delay(Duration.ofSeconds(1)).block()
        def mockLogger = Mock(Logger)
        pingService.log = mockLogger


        def mockClient = Mock(WebClient.RequestBodyUriSpec)
        def mockHeadersSpec = Mock(WebClient.RequestBodySpec)
        def mockResponseSpec = Mock(WebClient.ResponseSpec)

        // Mock WebClient 调用链
        pingService.client.post() >> mockClient
        mockClient.uri(pingService.pongUrl) >> mockHeadersSpec
        mockHeadersSpec.accept(_) >> mockHeadersSpec
        mockHeadersSpec.bodyValue("Hello") >> mockHeadersSpec  // 设置请求体为 "Hello"
        mockHeadersSpec.retrieve() >> mockResponseSpec
        mockResponseSpec.onStatus(_, _) >> mockResponseSpec
        mockResponseSpec.bodyToMono(String.class) >> Mono.error(new TimeoutException("Connection timed out"))

        when: "调用 pingPongService 方法"
        pingService.pingPongService()

        then: "验证超时错误日志"
        1 * mockLogger.info("Request not sent due to being 'rate limited' or other error: {}", "Connection timed out")
    }
}
