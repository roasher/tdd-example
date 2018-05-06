package ru.yurkinsworkshop.tddexample.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yurkinsworkshop.tddexample.client.VozovozClient;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;
import ru.yurkinsworkshop.tddexample.dto.Update;
import ru.yurkinsworkshop.tddexample.service.exception.VozovozException;
import ru.yurkinsworkshop.tddexample.service.manualexclusion.ManualExclusionService;

@Service
@AllArgsConstructor
@Slf4j
public class UpdateProcessorService {

    private final VozovozClient vozovozClient;
    private final ManualExclusionService manualExclusionService;
    private final AvailabilityNotifier availabilityNotifier;

    public void processUpdate(Update update) {
        if (update.getProductQuantity() <= 0) {
            availabilityNotifier.notify(getNotAvailableProduct(update.getProductId()));
            return;
        }
        if ("Blue".equals(update.getColor())) {
            availabilityNotifier.notify(getNotAvailableProduct(update.getProductId()));
            return;
        }
        if (!manualExclusionService.isProductEnabled(update.getProductId())) {
            availabilityNotifier.notify(getNotAvailableProduct(update.getProductId()));
            return;
        }
        try {
            final boolean availableForTransportation = vozovozClient.isAvailableForTransportation(update.getProductId());
            availabilityNotifier.notify(new ProductAvailability(update.getProductId(), availableForTransportation));
        } catch (Exception exception) {
            log.warn("Problems communicating with Vozovoz", exception);
            throw new VozovozException();
        }
    }

    private ProductAvailability getNotAvailableProduct(Long productId) {
        return new ProductAvailability(productId, false);
    }

}
