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
import java.sql.SQLException;


@SpringBootApplication
@ComponentScan({"ru.vtb.at.controllers"})
@MapperScan(value = "ru.vtb.at.mappers", sqlSessionFactoryRef = "sqlSessionFactory")
public class StandComparerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StandComparerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(StandComparerApplication.class, args);
    }

    @Value("${dp.host}")
    String dpHost;
    @Value("${dp.port}")
    String dpPort;
    @Value("${dp.username}")
    String dpLog;
    @Value("${dp.password}")
    String dpPass;

    @Bean
    public DataSource dataSource() throws SQLException {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/dp", dpHost, dpPort));
        ds.setUsername(dpLog);
        ds.setPassword(dpPass);
        ds.setMinimumIdle(3);
        ds.setMaximumPoolSize(10);
        ds.setLoginTimeout(15);
        return ds;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLogImpl(Log4j2Impl.class);
        factoryBean.setConfiguration(configuration);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml"));
        return factoryBean.getObject();
    }

    @Bean()
    StandComparer standComparer() {
        return new StandComparer();
    }


}
