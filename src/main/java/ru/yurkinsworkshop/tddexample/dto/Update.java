package ru.yurkinsworkshop.tddexample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Update {

    private long productId;
    private long productQuantity;

}
