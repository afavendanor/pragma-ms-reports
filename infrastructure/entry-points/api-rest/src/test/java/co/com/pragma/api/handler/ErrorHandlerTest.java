package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private ErrorHandler<String> errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler<>();
    }

    @Test
    void shouldHandleSuccessfulResponse() {
        // Given
        GenericResponseDTO<String> successResponse = new GenericResponseDTO<>(
                HttpStatus.OK, ResponseCode.MRPO001, "Success data");
        Mono<GenericResponseDTO<String>> successMono = Mono.just(successResponse);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(successMono, "testMethod"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK.value(), response.getResponseCode());
                    assertEquals("Success data", response.getData());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleDuplicateEntryException() {
        // Given
        DuplicateEntryException exception = new DuplicateEntryException(ResponseCode.MRPO003);
        Mono<GenericResponseDTO<String>> errorMono = Mono.error(exception);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(errorMono, "testMethod"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CONFLICT.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO003.getMessage(), response.getResponseMessage());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleFieldErrorException() {
        // Given
        FieldErrorException exception = new FieldErrorException(ResponseCode.MRPO002);
        Mono<GenericResponseDTO<String>> errorMono = Mono.error(exception);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(errorMono, "testMethod"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO002.getMessage(), response.getResponseMessage());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleInternalErrorException() {
        // Given
        List<FieldError> fieldErrors = Collections.emptyList();
        InternalErrorException exception = new InternalErrorException(ResponseCode.MRPO000);
        Mono<GenericResponseDTO<String>> errorMono = Mono.error(exception);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(errorMono, "testMethod"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO000.getMessage(), response.getResponseMessage());
                    assertEquals(fieldErrors, response.getFieldErrors());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleNotFoundException() {
        // Given
        List<FieldError> fieldErrors = Collections.emptyList();
        NotFoundException exception = new NotFoundException(ResponseCode.MRPO004);
        Mono<GenericResponseDTO<String>> errorMono = Mono.error(exception);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(errorMono, "testMethod"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO004.getMessage(), response.getResponseMessage());
                    assertEquals(fieldErrors, response.getFieldErrors());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleLoginException() {
        // Given
        LoginException exception = new LoginException(ResponseCode.MRPO005);
        Mono<GenericResponseDTO<String>> errorMono = Mono.error(exception);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(errorMono, "testMethod"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO005.getMessage(), response.getResponseMessage());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleDependencyException() {
        // Given
        DependencyException exception = new DependencyException(ResponseCode.MRPO003);
        Mono<GenericResponseDTO<String>> errorMono = Mono.error(exception);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(errorMono, "testMethod"))
                .assertNext(response -> {
                    assertEquals(HttpStatus.FAILED_DEPENDENCY.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO003.getMessage(), response.getResponseMessage());
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateUnhandledException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Unhandled exception");
        Mono<GenericResponseDTO<String>> errorMono = Mono.error(exception);

        // When & Then
        StepVerifier.create(errorHandler.addErrors(errorMono, "testMethod"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
