package com.savelms.api.exception.error;

import com.savelms.api.constant.ErrorCode;
import com.savelms.api.exception.GeneralException;
import com.savelms.api.exception.error.dto.APIErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice(annotations = RestController.class)
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        return callSuperInternalExceptionHandler(e, ErrorCode.VALIDATION_ERROR, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> general(GeneralException e, WebRequest request) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        HttpStatus status = errorCode.isClientSideError() ?
                HttpStatus.BAD_REQUEST :
                HttpStatus.INTERNAL_SERVER_ERROR;

        return callSuperInternalExceptionHandler(e, errorCode, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return callSuperInternalExceptionHandler(e, errorCode, HttpHeaders.EMPTY, httpStatus, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorCode errorCode = status.is4xxClientError() ?
                ErrorCode.SPRING_BAD_REQUEST :
                ErrorCode.SPRING_INTERNAL_ERROR;

        return super.handleExceptionInternal(
                e,
                APIErrorResponse.of(false, errorCode.getCode(), errorCode.getMessage(e)),
                headers,
                status,
                request
        );
    }

    private ResponseEntity<Object> callSuperInternalExceptionHandler(Exception ex, ErrorCode errorCode, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(
                ex,
                APIErrorResponse.of(false, errorCode.getCode(), errorCode.getMessage(ex)),
                headers,
                status,
                request
        );
    }

}
