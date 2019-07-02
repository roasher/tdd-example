package ru.yurkinsworkshop.tddexample.configuration.dostavchenko;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "dostavchenko")
@Data
@Validated
public class DostavchenkoConfig {

    @NotBlank
    private String endpoint;

}
