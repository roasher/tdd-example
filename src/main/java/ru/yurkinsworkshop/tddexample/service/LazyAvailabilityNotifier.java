package ru.yurkinsworkshop.tddexample.service;

import org.springframework.stereotype.Component;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;
import ru.yurkinsworkshop.tddexample.service.notifier.AvailabilityNotifier;

@Component
public class LazyAvailabilityNotifier implements AvailabilityNotifier {
    @Override
    public void notify(ProductAvailability productAvailability) {

    }
}
