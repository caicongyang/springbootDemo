package com.caicongyang.db.routing;

import com.caicongyang.db.routing.anno.ReadOnly;
import com.caicongyang.db.routing.anno.WriteOnly;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAnnoAspect {


    @Before("@annotation(readOnly)")
    public void setReadOnlyDataSource(ReadOnly readOnly) {
        DataSourceContextHolder.setDataSourceType("read");
    }

    @Before("@annotation(writeOnly)")
    public void setWriteOnlyDataSource(WriteOnly writeOnly) {
        DataSourceContextHolder.setDataSourceType("write");
    }

    @AfterReturning("@annotation(readOnly) || @annotation(writeOnly)")
    public void clearDataSourceType() {
        DataSourceContextHolder.clearDataSourceType();
    }
}