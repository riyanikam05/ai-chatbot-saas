package com.riya.aichatbot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class, FlywayAutoConfiguration.class})
@ActiveProfiles("test")
class AiChatbotSaasApplicationTests {

    @Test
    void contextLoads() {
    }
}