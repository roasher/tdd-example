package ru.yurkinsworkshop.tddexample.service.notifier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;
import ru.yurkinsworkshop.tddexample.service.exception.DataCommunicationException;
import ru.yurkinsworkshop.tddexample.service.notifier.lazy.AvailabilityPersistenceObject;
import ru.yurkinsworkshop.tddexample.service.notifier.lazy.AvailabilityRepository;

@Component
@AllArgsConstructor
@Slf4j
public class LazyAvailabilityNotifier implements AvailabilityNotifier {

    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityNotifier availabilityNotifier;

    @Override
    public void notify(ProductAvailability productAvailability) {
        final AvailabilityPersistenceObject persistedProductAvailability = availabilityRepository
                .findByProductId(productAvailability.getProductId());
        if (persistedProductAvailability == null) {
            notifyWith(productAvailability);
            availabilityRepository.save(createObjectFromProductAvailability(productAvailability));
        } else if (persistedProductAvailability.isAvailability() != productAvailability.isAvailable()) {
            notifyWith(productAvailability);
            persistedProductAvailability.setAvailability(productAvailability.isAvailable());
            availabilityRepository.save(persistedProductAvailability);
        }
    }

    private void notifyWith(ProductAvailability productAvailability) {
        try {
            availabilityNotifier.notify(productAvailability);
        } catch (RestClientException exception) {
            log.error("Couldn't notify", exception);
            throw new DataCommunicationException();
        }
    }


    private AvailabilityPersistenceObject createObjectFromProductAvailability(ProductAvailability productAvailability) {
        return new AvailabilityPersistenceObject(productAvailability.getProductId(), productAvailability.isAvailable());
    }

}
