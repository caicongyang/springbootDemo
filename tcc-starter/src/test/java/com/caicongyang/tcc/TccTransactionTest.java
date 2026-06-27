package com.caicongyang.tcc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest(classes = TccTransactionTest.TestConfig.class,
        properties = "tcc.enabled=true")
class TccTransactionTest {

    @Autowired
    private TestTccService testTccService;

    @BeforeEach
    void reset() {
        testTccService.reset();
    }

    @Test
    void shouldConfirmOnSuccess() {
        String result = testTccService.doWithConfirm();
        assertThat(result).isEqualTo("try-ok");
        assertThat(testTccService.isConfirmed()).isTrue();
        assertThat(testTccService.isCancelled()).isFalse();
    }

    @Test
    void shouldCancelOnFailure() {
        assertThatThrownBy(() -> testTccService.doWithCancel())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("try failed");
        assertThat(testTccService.isCancelled()).isTrue();
        assertThat(testTccService.isConfirmed()).isFalse();
    }

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        public TccCoordinator tccCoordinator() {
            return new TccCoordinator();
        }

        @Bean
        public TccAspect tccAspect(TccCoordinator tccCoordinator) {
            return new TccAspect(tccCoordinator);
        }

        @Bean
        public TestTccService testTccService() {
            return new TestTccService();
        }
    }

    static class TestTccService {
        private boolean confirmed;
        private boolean cancelled;

        public boolean isConfirmed() {
            return confirmed;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void reset() {
            this.confirmed = false;
            this.cancelled = false;
        }

        @TccTransaction(confirmMethod = "doConfirm", cancelMethod = "doCancel")
        public String doWithConfirm() {
            return "try-ok";
        }

        @TccTransaction(confirmMethod = "doConfirm", cancelMethod = "doCancel")
        public String doWithCancel() {
            throw new RuntimeException("try failed");
        }

        public void doConfirm() {
            this.confirmed = true;
        }

        public void doCancel() {
            this.cancelled = true;
        }
    }
}
