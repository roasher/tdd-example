package ru.yurkinsworkshop.tddexample.service.manualexclusion;

import org.springframework.stereotype.Service;

@Service
public class ManualExclusionService {

    public boolean isProductEnabled(Long productId) {
        return false;
    }

    public void disableProduct(long productId) {
        // TODO impl
    }

}
