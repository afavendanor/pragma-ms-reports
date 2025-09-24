package co.com.pragma.api;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.api.dto.LoanStatsDTO;
import co.com.pragma.api.handler.LoanApplicationHandler;
import co.com.pragma.model.error.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationControllerTest {

    @Mock
    private LoanApplicationHandler loanApplicationHandler;

    private LoanApplicationController loanApplicationController;

    @BeforeEach
    void setUp() {
        loanApplicationController = new LoanApplicationController(loanApplicationHandler);
    }

    @Test
    void shouldObtainSummarySuccessfully() {
        // Given
        LoanStatsDTO loanStatsDTO = new LoanStatsDTO(10, 500000.0);
        GenericResponseDTO<LoanStatsDTO> genericResponse = new GenericResponseDTO<>(
                HttpStatus.OK, ResponseCode.MRPO001, loanStatsDTO);

        when(loanApplicationHandler.obtainSummary())
                .thenReturn(Mono.just(genericResponse));

        // When & Then
        StepVerifier.create(loanApplicationController.obtainSummary())
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(genericResponse, responseEntity.getBody());
                    assertEquals(loanStatsDTO, responseEntity.getBody().getData());
                })
                .verifyComplete();

        verify(loanApplicationHandler, times(1)).obtainSummary();
    }

    @Test
    void shouldHandleNotFoundResponse() {
        // Given
        GenericResponseDTO<LoanStatsDTO> notFoundResponse = new GenericResponseDTO<>(
                HttpStatus.NOT_FOUND.value(), "No data found", null, null);

        when(loanApplicationHandler.obtainSummary())
                .thenReturn(Mono.just(notFoundResponse));

        // When & Then
        StepVerifier.create(loanApplicationController.obtainSummary())
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
                    assertEquals(notFoundResponse, responseEntity.getBody());
                })
                .verifyComplete();

        verify(loanApplicationHandler, times(1)).obtainSummary();
    }

    @Test
    void shouldHandleInternalServerErrorResponse() {
        // Given
        GenericResponseDTO<LoanStatsDTO> errorResponse = new GenericResponseDTO<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal error", null, null);

        when(loanApplicationHandler.obtainSummary())
                .thenReturn(Mono.just(errorResponse));

        // When & Then
        StepVerifier.create(loanApplicationController.obtainSummary())
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
                    assertEquals(errorResponse, responseEntity.getBody());
                })
                .verifyComplete();

        verify(loanApplicationHandler, times(1)).obtainSummary();
    }

    @Test
    void shouldHandleBadRequestResponse() {
        // Given
        GenericResponseDTO<LoanStatsDTO> badRequestResponse = new GenericResponseDTO<>(
                HttpStatus.BAD_REQUEST.value(), "Invalid request", null, null);

        when(loanApplicationHandler.obtainSummary())
                .thenReturn(Mono.just(badRequestResponse));

        // When & Then
        StepVerifier.create(loanApplicationController.obtainSummary())
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
                    assertEquals(badRequestResponse, responseEntity.getBody());
                })
                .verifyComplete();

        verify(loanApplicationHandler, times(1)).obtainSummary();
    }

    @Test
    void shouldHandleEmptyStatsResponse() {
        // Given
        LoanStatsDTO emptyStatsDTO = new LoanStatsDTO(0, 0.0);
        GenericResponseDTO<LoanStatsDTO> emptyResponse = new GenericResponseDTO<>(
                HttpStatus.OK, ResponseCode.MRPO001, emptyStatsDTO);

        when(loanApplicationHandler.obtainSummary())
                .thenReturn(Mono.just(emptyResponse));

        // When & Then
        StepVerifier.create(loanApplicationController.obtainSummary())
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(0, responseEntity.getBody().getData().totalSolicitudes());
                    assertEquals(0.0, responseEntity.getBody().getData().montoTotal());
                })
                .verifyComplete();

        verify(loanApplicationHandler, times(1)).obtainSummary();
    }
}
