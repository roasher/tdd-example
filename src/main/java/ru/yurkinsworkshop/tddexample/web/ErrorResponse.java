package ru.yurkinsworkshop.tddexample.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private List<Message> errors;

    @Data
    @AllArgsConstructor
    public static class Message {
        String message;
    }

}
