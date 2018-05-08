package ru.yurkinsworkshop.tddexample.configuration.vozovoz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VozovozConfiguration {

    @Bean
    public RestTemplate vozovozRestTemplate() {
        return new RestTemplate();
    }

}
