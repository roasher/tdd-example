package ru.yurkinsworkshop.tddexample.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.yurkinsworkshop.tddexample.configuration.data.DataConfig;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;
import ru.yurkinsworkshop.tddexample.service.notifier.AvailabilityNotifier;

@Component
@AllArgsConstructor
public class DataClient implements AvailabilityNotifier {

    private final RestTemplate dataRestTemplate;
    private final DataConfig dataConfig;

    @Override
    public void notify(ProductAvailability productAvailability) {
        dataRestTemplate.postForLocation(dataConfig.getEndpoint(), productAvailability);
    }

}
