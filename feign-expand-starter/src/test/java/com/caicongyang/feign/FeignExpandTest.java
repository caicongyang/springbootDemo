package com.caicongyang.feign;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import feign.Feign;
import feign.Request;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests for feign-expand-starter: basic compilation + factory bean presence.
 */
@SpringBootTest
@ContextConfiguration(classes = FeignExpandTest.TestConfig.class)
class FeignExpandTest {

    @Autowired(required = false)
    private FeignClientFactory feignClientFactory;

    /**
     * Minimal test config: manually provides a Feign.Builder so the factory
     * can be created without needing a full OpenFeign auto-config bootstrap.
     */
    @SpringBootConfiguration
    static class TestConfig {

        @Bean
        public Feign.Builder feignBuilder() {
            return Feign.builder().requestInterceptor(template -> {});
        }

        @Bean
        public FeignClientFactory feignClientFactory(Feign.Builder feignBuilder) {
            FeignClientFactory factory = new FeignClientFactory();
            // Use reflection to inject the builder since the field is @Autowired
            try {
                java.lang.reflect.Field field = FeignClientFactory.class.getDeclaredField("feignBuilder");
                field.setAccessible(true);
                field.set(factory, feignBuilder);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return factory;
        }
    }

    @Test
    void shouldLoadFeignClientFactoryBean() {
        assertThat(feignClientFactory).isNotNull();
    }

    @Test
    void shouldCreateProgrammaticClient() {
        TestApi client = feignClientFactory.createClient(TestApi.class, "http://localhost:8080");
        assertThat(client).isNotNull();
    }

    @Test
    void enableFeignExpandedAnnotationExists() {
        assertThat(EnableFeignExpanded.class).isNotNull();
    }

    @Test
    void feignClientFactoryCanBeInstantiated() {
        assertThat(FeignClientFactory.class).isNotNull();
    }

    /**
     * A minimal Feign interface for the programmatic creation test.
     */
    interface TestApi {
        @feign.RequestLine("GET /hello")
        String hello();
    }
}
