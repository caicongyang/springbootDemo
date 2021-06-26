package com.caicongyang.stock.component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrometheusCustomMonitor {


    /**
     *  注册失败数
     */
    private Counter studentAddCountError;

    /**
     * 注册成功数
     */
    private Counter studentAddCount;

    private final MeterRegistry registry;

    @Autowired
    public PrometheusCustomMonitor(MeterRegistry registry) {
        this.registry = registry;
    }



    @PostConstruct
    private void init() {
        studentAddCountError = registry.counter("student_register_requests_total", "status", "error");
        studentAddCount = registry.counter("student_register_requests_total", "status", "all");
    }


    public Counter getStudentAddCountError() {
        return studentAddCountError;
    }

    public Counter getStudentAddCount() {
        return studentAddCount;
    }
}
