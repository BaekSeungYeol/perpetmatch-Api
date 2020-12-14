package com.perpetmatch;

import com.perpetmatch.infra.config.AppProperties;
import com.perpetmatch.jjwt.JwtTokenProvider;
import com.perpetmatch.jjwt.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PerpetmatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerpetmatchApplication.class, args);
    }


    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            JwtTokenProvider tokenProvider;
            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                System.out.println(appProperties.getOauth2().getAuthorizedRedirectUris());
                System.out.println(appProperties.getAuth().getJwtExpirationInMs());
                System.out.println(appProperties.getAuth().getJwtSecret());
            }
        };
    }

}
