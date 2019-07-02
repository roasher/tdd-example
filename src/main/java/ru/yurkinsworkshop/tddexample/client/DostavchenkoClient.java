package ru.yurkinsworkshop.tddexample.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.yurkinsworkshop.tddexample.configuration.dostavchenko.DostavchenkoConfig;

@Component
@AllArgsConstructor
public class DostavchenkoClient {

    private final RestTemplate dostavchenkoRestTemplate;
    private final DostavchenkoConfig dostavchenkoConfig;

    public boolean isAvailableForTransportation(Long productId) {
        final String response = dostavchenkoRestTemplate.getForObject(dostavchenkoConfig.getEndpoint(), String.class, productId);
        return Boolean.parseBoolean(response);
    }

}
