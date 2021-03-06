package ru.yurkinsworkshop.tddexample.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yurkinsworkshop.tddexample.client.DostavchenkoClient;
import ru.yurkinsworkshop.tddexample.dto.ProductAvailability;
import ru.yurkinsworkshop.tddexample.dto.Update;
import ru.yurkinsworkshop.tddexample.service.exception.DostavchenkoException;
import ru.yurkinsworkshop.tddexample.service.manualexclusion.ManualExclusionService;
import ru.yurkinsworkshop.tddexample.service.notifier.AvailabilityNotifier;

@Service
@Slf4j
public class UpdateProcessorService {


    private final AvailabilityNotifier availabilityNotifier;
    private final DostavchenkoClient dostavchenkoClient;
    private final ManualExclusionService manualExclusionService;

    public UpdateProcessorService(DostavchenkoClient dostavchenkoClient, ManualExclusionService manualExclusionService,
                                  @Qualifier("lazyAvailabilityNotifier") AvailabilityNotifier availabilityNotifier) {
        this.dostavchenkoClient = dostavchenkoClient;
        this.manualExclusionService = manualExclusionService;
        this.availabilityNotifier = availabilityNotifier;
    }

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
            final boolean availableForTransportation = dostavchenkoClient.isAvailableForTransportation(update.getProductId());
            availabilityNotifier.notify(new ProductAvailability(update.getProductId(), availableForTransportation));
        } catch (Exception exception) {
            log.warn("Problems communicating with DostavchenKO", exception);
            throw new DostavchenkoException();
        }
    }

    private ProductAvailability getNotAvailableProduct(Long productId) {
        return new ProductAvailability(productId, false);
    }

}
