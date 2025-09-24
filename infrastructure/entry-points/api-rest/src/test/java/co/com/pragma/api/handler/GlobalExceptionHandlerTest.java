package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.ResponseCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleConstraintViolationException() {
        // Arrange
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("campo");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("mensaje de error");

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        // Act
        Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> responseMono = handler.handleConstraintViolationException(ex);

        // Assert
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    List<co.com.pragma.model.error.FieldError> errores = Objects.requireNonNull(response.getBody()).getFieldErrors();
                    assertEquals("mensaje de error", errores.getFirst().getError());
                    assertEquals("campo", errores.getFirst().getField());
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
                })
                .verifyComplete();
    }

    @Test
    void testHandleValidationException() {
        // Arrange
        ValidationException ex = new ValidationException("mensaje de prueba");

        // Act
        Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> responseMono = handler.handleValidationException(ex);

        // Assert
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    GenericResponseDTO<?> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("Argumento inválido", body.getResponseMessage());
                    assertNotNull(body.getFieldErrors());
                    assertEquals(1, body.getFieldErrors().size());
                    assertEquals("error", body.getFieldErrors().getFirst().getField());
                    assertEquals("mensaje de prueba", body.getFieldErrors().getFirst().getError());
                })
                .verifyComplete();
    }

    @Test
    void testHandleWebExchangeBindException() {
        // Arrange
        FieldError fieldError = new FieldError("objectName", "campo", "mensaje de error");

        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        when(ex.getBindingResult()).thenReturn(
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "target")
        );

        org.springframework.validation.BindingResult bindingResult = ex.getBindingResult();
        bindingResult.addError(fieldError);

        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Act
        Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> responseMono = handler.handleWebExchangeBindException(ex);

        // Assert
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    List<co.com.pragma.model.error.FieldError> errores = Objects.requireNonNull(response.getBody()).getFieldErrors();
                    assertEquals("mensaje de error", errores.getFirst().getError());
                    assertEquals("campo", errores.getFirst().getField());
                    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getResponseCode());
                })
                .verifyComplete();
    }

    @Test
    void testHandleMissingRequestValueException() {
        // Arrange
        ServerWebInputException exception = new ServerWebInputException("Campo 'id' es obligatorio");
        Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> responseMono =
                handler.handleMissingRequestValueException(exception);

        // Act & Assert
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    GenericResponseDTO<Map<String, String>> body = response.getBody();
                    assertNotNull(body);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), body.getResponseCode());
                    assertEquals("Argumento inválido", body.getResponseMessage());
                    assertNotNull(body.getFieldErrors());
                    assertFalse(body.getFieldErrors().isEmpty());
                    assertTrue(body.getFieldErrors().stream()
                            .anyMatch(err -> err.getError().contains("Campo 'id'")));
                })
                .verifyComplete();
    }

    @Test
    void handleAccessDeniedException_deberiaRetornarError() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        // Act
        Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> resultado = handler.handleAccessDeniedException(ex);

        // Assert
        StepVerifier.create(resultado)
                .assertNext(response -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getResponseCode());
                    assertEquals("Access denied", response.getBody().getResponseMessage());
                    assertNull(response.getBody().getData());
                })
                .verifyComplete();
    }

    @Test
    void handleException_deberiaRetornarError500() {
        // Arrange
        RuntimeException ex = new RuntimeException("Se ha generado un error, intente nuevamente");

        // Act
        Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> resultado = handler.handleException(ex);

        // Assert
        StepVerifier.create(resultado)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getResponseCode());
                    assertEquals(ResponseCode.MRPO000.getMessage(), response.getBody().getResponseMessage());
                    assertNull(response.getBody().getData());
                })
                .verifyComplete();
    }

}
