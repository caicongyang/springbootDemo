package com.caicongyang.core.aspect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(RequestLogAspectTest.TestConfig.class)
class RequestLogAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        RequestLogAspect requestLogAspect() {
            return new RequestLogAspect();
        }

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/test/get")
        public Map<String, String> getMethod() {
            return Map.of("status", "ok");
        }

        @PostMapping("/test/post")
        public Map<String, String> postMethod(@RequestBody Map<String, String> body) {
            return Map.of("received", body.getOrDefault("key", "none"));
        }

        @GetMapping("/test/error")
        public Map<String, String> errorMethod() {
            throw new RuntimeException("test exception");
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handleRuntime(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error: " + e.getMessage());
        }
    }

    @Test
    void getRequestShouldSucceed() throws Exception {
        mockMvc.perform(get("/test/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void postRequestShouldSucceed() throws Exception {
        mockMvc.perform(post("/test/post")
                        .contentType("application/json")
                        .content("{\"key\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.received").value("hello"));
    }

    @Test
    void errorRequestShouldReturn500() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError());
    }
}
