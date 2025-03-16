package ru.vtb.at;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import ru.vtb.at.stand_comparer.StandComparer;

import javax.sql.DataSource;



@SpringBootApplication
@ComponentScan({"ru.vtb.at.context"})
public class StandComparerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StandComparerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(StandComparerApplication.class, args);
    }

    @Bean()
    StandComparer standComparer() {
        return new StandComparer();
    }


}
