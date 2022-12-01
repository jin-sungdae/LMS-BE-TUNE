package com.savelms.api.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ErrorCodeTest {

//    @ParameterizedTest
//    @MethodSource
//    @NullAndEmptySource
//    @DisplayName("예외를 받으면 - 예외메시지가 포함된 메시지 출력")
//    void givenExceptionWithMessage_whenGettingMessage_thenReturnMessage(ErrorCode sut, String expected) {
//        // Given
//        Exception e = new Exception("This is test message.");
//
//        // When
//        String actual = sut.getMessage(e);
//
//        //Then
//        assertThat(actual).isEqualTo(expected);
//    }
//
//
//    static Stream<Arguments> givenExceptionWithMessage_whenGettingMessage_thenReturnMessage() {
//        return Stream.of(
//                arguments(ErrorCode.OK, "OK - This is test message."),
//                arguments(ErrorCode.BAD_REQUEST, "bad request - This is test message."),
//                arguments(ErrorCode.SPRING_BAD_REQUEST, "Spring-detected bad request - This is test message."),
//                arguments(ErrorCode.VALIDATION_ERROR, "Validation error - This is test message."),
//                arguments(ErrorCode.INTERNAL_ERROR, "internal error - This is test message."),
//                arguments(ErrorCode.SPRING_INTERNAL_ERROR, "Spring-detected internal error - This is test message."),
//                arguments(ErrorCode.DATA_ACCESS_ERROR, "Data access error - This is test message.")
//
//        );
//    }

    @DisplayName("ErrorCode.OK")
    @Test
    void ErrorCodeOK() {
        // Given
        Exception e = new Exception("This is test message.");

        // When
        String result = ErrorCode.OK.getMessage(e);

        //Then
        assertThat(result).isEqualTo("OK - This is test message.");
    }

    @DisplayName("ErrorCode.BAD_REQUEST")
    @Test
    void ErrorCodeBadRequest() {
        // Given
        Exception e = new Exception("This is test message.");

        // When
        String result = ErrorCode.BAD_REQUEST.getMessage(e);

        //Then
        assertThat(result).isEqualTo("bad request - This is test message.");
    }


    @DisplayName("ErrorCode.SPRING_BAD_REQUEST")
    @Test
    void ErrorCodeSpringBadRequest() {
        // Given
        Exception e = new Exception("This is test message.");

        // When
        String result = ErrorCode.SPRING_BAD_REQUEST.getMessage(e);

        //Then
        assertThat(result).isEqualTo("Spring-detected bad request - This is test message.");
    }

    @DisplayName("ErrorCode.VALIDATION_ERROR")
    @Test
    void ErrorCodeValidationError() {
        // Given
        Exception e = new Exception("This is test message.");

        // When
        String result = ErrorCode.VALIDATION_ERROR.getMessage(e);

        //Then
        assertThat(result).isEqualTo("Validation error - This is test message.");
    }


    @DisplayName("ErrorCode.INTERNAL_ERROR")
    @Test
    void ErrorCodeInternalError() {
        // Given
        Exception e = new Exception("This is test message.");

        // When
        String result = ErrorCode.INTERNAL_ERROR.getMessage(e);

        //Then
        assertThat(result).isEqualTo("internal error - This is test message.");
    }

    @DisplayName("ErrorCode.SPRING_INTERNAL_ERROR")
    @Test
    void ErrorCodeSpringInternalError() {
        // Given
        Exception e = new Exception("This is test message.");

        // When
        String result = ErrorCode.SPRING_INTERNAL_ERROR.getMessage(e);

        //Then
        assertThat(result).isEqualTo("Spring-detected internal error - This is test message.");
    }

    @DisplayName("ErrorCode.DATA_ACCESS_ERROR")
    @Test
    void ErrorCodeDataAccessError() {
        // Given
        Exception e = new Exception("This is test message.");

        // When
        String result = ErrorCode.DATA_ACCESS_ERROR.getMessage(e);

        //Then
        assertThat(result).isEqualTo("Data access error - This is test message.");
    }
}