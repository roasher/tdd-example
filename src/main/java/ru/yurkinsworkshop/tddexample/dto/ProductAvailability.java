package ru.yurkinsworkshop.tddexample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductAvailability {

    private long productId;
    private boolean isAvailable;

}
