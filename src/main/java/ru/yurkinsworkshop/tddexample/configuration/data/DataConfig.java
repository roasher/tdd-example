package ru.yurkinsworkshop.tddexample.configuration.data;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "data")
@Validated
@Data
public class DataConfig {

    @NotBlank
    private String url;

}
