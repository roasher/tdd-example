package ru.yurkinsworkshop.tddexample.configuration.dostavchenko;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DostavchenkoConfiguration {

    @Bean
    public RestTemplate dostavchenkoRestTemplate() {
        return new RestTemplate();
    }

}
