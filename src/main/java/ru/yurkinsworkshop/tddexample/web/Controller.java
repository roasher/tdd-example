package ru.yurkinsworkshop.tddexample.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yurkinsworkshop.tddexample.dto.Update;
import ru.yurkinsworkshop.tddexample.service.Service;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@AllArgsConstructor
@Validated
@Slf4j
public class Controller {

    private final Service service;

    @PostMapping("/product-quantity-update")
    public void updateQuantity(@RequestBody @Valid Update update) {
        service.processUpdate(update);
    }

    @PostMapping("/disableProduct")
    public void disableProduct(@RequestParam("productId") @Min(0) Long productId) {
        service.disableProduct(Long.valueOf(productId));
    }

}
