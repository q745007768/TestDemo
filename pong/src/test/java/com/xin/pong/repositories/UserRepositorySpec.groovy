package com.xin.pong.repositories

import com.xin.pong.model.User
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.Duration

@SpringBootTest
class UserRepositorySpec extends Specification {
    @Autowired
    UserRepository userRepository;

    def "创建用户成功"() {
        User user = new User(1, "张三")
        when:
        Mono response = userRepository.save(user)

        then:
        StepVerifier
                .create(response)
                .expectNextMatches { it.id == 1 && it.username == "张三" }
                .verifyComplete()
    }


    def "查询用户成功"() {
        when:
        def response = userRepository.findById(1)

        then:
        StepVerifier
                .create(response)
                .expectNextMatches { it.id == 1 && it.username == "张三" }
                .verifyComplete()
    }

    def "查询用户失败"() {
        when:
        def response = userRepository.findById(2)

        then:
        StepVerifier
                .create(response)
                .expectNextCount(0)
                .verifyComplete()
    }

    def "修改用户成功"() {
        when:
        def response = userRepository.save(new User(1, "李四"))

        then:
        StepVerifier
                .create(response)
                .expectNextMatches { it.id == 1 && it.username == "李四" }
                .verifyComplete()
    }

    def "修改用户失败"() {
        when:
        def response = userRepository.save(new User("张三"))

        then:
        StepVerifier
                .create(response)
                .expectError()
    }

    def "删除用户成功"() {
        when:
        def response = userRepository.deleteById(1)

        then:
        StepVerifier.create(response)
                .verifyComplete()
    }
}
