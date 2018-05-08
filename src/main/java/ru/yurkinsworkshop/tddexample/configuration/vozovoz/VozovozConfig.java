package ru.yurkinsworkshop.tddexample.configuration.vozovoz;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vozovoz")
@Data
public class VozovozConfig {

    @NotBlank
    private String endpoint;

}
