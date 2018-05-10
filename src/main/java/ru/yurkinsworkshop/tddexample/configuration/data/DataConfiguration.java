package ru.yurkinsworkshop.tddexample.configuration.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DataConfiguration {

    @Bean
    public RestTemplate dataRestTemplate() {
        return new RestTemplate();
    }

}
