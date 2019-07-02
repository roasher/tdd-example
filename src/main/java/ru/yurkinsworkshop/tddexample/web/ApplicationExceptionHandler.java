package ru.yurkinsworkshop.tddexample.web;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yurkinsworkshop.tddexample.service.exception.DataCommunicationException;
import ru.yurkinsworkshop.tddexample.service.exception.DostavchenkoException;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintViolationException(ConstraintViolationException exception) {
        log.info("Constraint Violation", exception);
        return new ErrorResponse(exception.getConstraintViolations().stream()
                .map(constraintViolation -> new ErrorResponse.Message(
                        ((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().toString() +
                                " is invalid"))
                .collect(Collectors.toList()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.info(exception.getMessage());
        List<ErrorResponse.Message> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.Message(fieldError.getField() + " is invalid"))
                .collect(Collectors.toList());
        return new ErrorResponse(fieldErrors);
    }

    @ExceptionHandler(DostavchenkoException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse onDostavchenkoCommunicationException(DostavchenkoException exception) {
        log.error("DostavchenKO communication exception", exception);
        return new ErrorResponse(Collections.singletonList(
                new ErrorResponse.Message("DostavchenKO communication exception")));
    }

    @ExceptionHandler(DataCommunicationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse onDataCommunicationException(DataCommunicationException exception) {
        log.error("DostavchenKO communication exception", exception);
        return new ErrorResponse(Collections.singletonList(
                new ErrorResponse.Message("Can't communicate with Data system")));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse onException(Exception exception) {
        log.error("Error while processing", exception);
        return new ErrorResponse(Collections.singletonList(
                new ErrorResponse.Message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())));
    }
}
