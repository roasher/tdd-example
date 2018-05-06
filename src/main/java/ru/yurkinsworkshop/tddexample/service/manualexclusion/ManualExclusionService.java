package ru.yurkinsworkshop.tddexample.service.manualexclusion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ManualExclusionService {

    private final ManualExclusionRepository manualExclusionRepository;

    public boolean isProductEnabled(Long productId) {
        return !manualExclusionRepository.exists(productId);
    }

    public void disableProduct(long productId) {
        manualExclusionRepository.save(new ManualExclusion(productId));
    }

}
