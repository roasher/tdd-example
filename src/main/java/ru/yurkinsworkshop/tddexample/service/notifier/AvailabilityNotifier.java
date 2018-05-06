package ru.yurkinsworkshop.tddexample.service.notifier;

import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;

public interface AvailabilityNotifier {
    void notify(ProductAvailability productAvailability);
}
