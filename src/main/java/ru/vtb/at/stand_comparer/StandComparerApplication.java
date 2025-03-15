package ru.vtb.at.stand_comparer;

import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;


@MapperScan(value = "mappers.dp", sqlSessionFactoryRef = "sqlSessionFactory")
@SpringBootApplication
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

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        //factoryBean.setDataSource(dataSourceDP());
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLogImpl(Log4j2Impl.class);
        factoryBean.setConfiguration(configuration);
        //factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml"));
        return factoryBean.getObject();
    }


}
