package com.caicongyang.db.routing.config;

import com.caicongyang.db.routing.DataSourceAnnoAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@ConditionalOnClass(DataSource.class)
@Import({ReadWriteDataSourceConfig.class, DataSourceAnnoAspect.class,
})
@Configuration
public class ReadWriteSplitAutoConfiguration {
}
