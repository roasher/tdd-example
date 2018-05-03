package ru.yurkinsworkshop.tddexample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Update {

    @Min(0)
    @NotNull
    private Long productId;
    @Min(0)
    @NotNull
    private Long productQuantity;
    @NotBlank
    private String color;

}
