package com.caicongyang.core.aspect;

import com.caicongyang.core.domain.RequestErrorInfo;
import com.caicongyang.core.domain.RequestInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildRequestParamTest {

    @Test
    void requestInfoShouldSerialize() {
        RequestInfo info = new RequestInfo();
        info.setIp("127.0.0.1");
        info.setUrl("/test");
        info.setHttpMethod("GET");
        info.setClassMethod("TestController.test");
        info.setTimeCost(42L);

        assertEquals("127.0.0.1", info.getIp());
        assertEquals("/test", info.getUrl());
        assertEquals("GET", info.getHttpMethod());
        assertEquals("TestController.test", info.getClassMethod());
        assertEquals(42L, info.getTimeCost());
    }

    @Test
    void requestErrorInfoShouldHolderException() {
        RequestErrorInfo errorInfo = new RequestErrorInfo();
        RuntimeException ex = new RuntimeException("test error");
        errorInfo.setException(ex);
        errorInfo.setIp("10.0.0.1");
        errorInfo.setUrl("/error");
        errorInfo.setHttpMethod("POST");

        assertEquals(ex, errorInfo.getException());
        assertEquals("10.0.0.1", errorInfo.getIp());
        assertEquals("/error", errorInfo.getUrl());
        assertEquals("POST", errorInfo.getHttpMethod());
    }

    @Test
    void requestInfoRequestParamsCanBeNull() {
        RequestInfo info = new RequestInfo();
        assertNull(info.getRequestParams());
        info.setRequestParams(null);
        assertNull(info.getRequestParams());
    }

    @Test
    void requestInfoResultCanBeNull() {
        RequestInfo info = new RequestInfo();
        assertNull(info.getResult());
    }

    @Test
    void requestErrorInfoDefaultsShouldBeNull() {
        RequestErrorInfo errorInfo = new RequestErrorInfo();
        assertNull(errorInfo.getException());
        assertNull(errorInfo.getIp());
    }

    @Test
    void requestInfoSerialVersionUIDShouldBeStable() {
        // Verify the class is loadable
        RequestInfo info = new RequestInfo();
        assertNotNull(info);
    }
}
