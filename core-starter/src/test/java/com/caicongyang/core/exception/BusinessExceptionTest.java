package com.caicongyang.core.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void shouldHaveErrorCode() {
        BusinessException ex = new BusinessException(404, "not found");
        assertEquals(404, ex.getErrCode());
        assertEquals("not found", ex.getMessage());
    }

    @Test
    void shouldBeRuntimeException() {
        BusinessException ex = new BusinessException(500, "error");
        assertInstanceOf(RuntimeException.class, ex);
    }
}
