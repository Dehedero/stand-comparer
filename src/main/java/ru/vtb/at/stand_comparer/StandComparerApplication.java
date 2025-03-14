package ru.vtb.at.stand_comparer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;


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


}
