package ru.vtb.at.context;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(value = "mappers.dp", sqlSessionFactoryRef = "sqlSessionFactory")
@Lazy
public class context {
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
}
