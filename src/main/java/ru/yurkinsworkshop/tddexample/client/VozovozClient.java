package ru.yurkinsworkshop.tddexample.client;

import org.springframework.stereotype.Component;

@Component
public class VozovozClient {
    public boolean isAvailableForTransportation(Long productId) {
        return false;
    }
}
