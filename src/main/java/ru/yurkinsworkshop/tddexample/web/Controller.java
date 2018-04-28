package ru.yurkinsworkshop.tddexample.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yurkinsworkshop.tddexample.service.Service;

@RestController
@AllArgsConstructor
public class Controller {

    private final Service service;

}
