package ru.yurkinsworkshop.tddexample.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.yurkinsworkshop.tddexample.configuration.vozovoz.VozovozConfig;

@Component
@AllArgsConstructor
public class VozovozClient {

    private final RestTemplate vozovozRestTemplate;
    private final VozovozConfig vozovozConfig;

    public boolean isAvailableForTransportation(Long productId) {
        final String response = vozovozRestTemplate.getForObject(vozovozConfig.getEndpoint(), String.class, productId);
        return Boolean.parseBoolean(response);
    }

}
