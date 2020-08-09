package com.perpetmatch;

import com.perpetmatch.infra.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class PerpetmatchApplication {

//    @PostConstruct
//    void init() {
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//    }

    public static void main(String[] args) {
        SpringApplication.run(PerpetmatchApplication.class, args);
    }

}
