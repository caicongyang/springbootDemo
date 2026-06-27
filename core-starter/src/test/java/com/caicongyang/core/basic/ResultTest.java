package com.caicongyang.core.basic;

import com.caicongyang.core.exception.BusinessException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void okShouldReturnSuccessCode() {
        Result<String> result = Result.ok("data");
        assertEquals(0, result.getCode());
        assertEquals("Success", result.getMessage());
        assertEquals("data", result.getData());
    }

    @Test
    void okWithoutDataShouldReturnNullData() {
        Result<String> result = Result.ok();
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    void failShouldReturnFailedCode() {
        Result<String> result = Result.fail("some error");
        assertEquals(1, result.getCode());
        assertEquals("some error", result.getMessage());
    }

    @Test
    void failWithCodeShouldReturnCustomCode() {
        Result<String> result = Result.fail(400, "bad request");
        assertEquals(400, result.getCode());
        assertEquals("bad request", result.getMessage());
    }

    @Test
    void failWithExceptionShouldExtractMessage() {
        BusinessException ex = new BusinessException(403, "forbidden");
        Result<String> result = Result.fail(ex);
        assertEquals(1, result.getCode());
        assertEquals("forbidden", result.getMessage());
    }
}
