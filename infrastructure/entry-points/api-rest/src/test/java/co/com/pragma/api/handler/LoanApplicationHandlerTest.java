package co.com.pragma.api.handler;

import co.com.pragma.api.dto.LoanStatsDTO;
import co.com.pragma.api.mapper.LoanApplicationApiRestMapper;
import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanStats;
import co.com.pragma.usecase.ObtainSumaryApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationHandlerTest {

    @Mock
    private ObtainSumaryApplicationUseCase obtainSumaryApplicationUseCase;

    @Mock
    private LoanApplicationApiRestMapper loanApplicationApiRestMapper;

    private LoanApplicationHandler loanApplicationHandler;

    @BeforeEach
    void setUp() {
        loanApplicationHandler = new LoanApplicationHandler(
                obtainSumaryApplicationUseCase,
                loanApplicationApiRestMapper
        );
    }

    @Test
    void shouldObtainSummarySuccessfully() {
        // Given
        LoanStats loanStats = new LoanStats(5, 250000.0);
        LoanStatsDTO loanStatsDTO = new LoanStatsDTO(5, 250000.0);

        when(obtainSumaryApplicationUseCase.execute())
                .thenReturn(Mono.just(loanStats));
        when(loanApplicationApiRestMapper.loanStatsToLoanStatsDTO(loanStats))
                .thenReturn(loanStatsDTO);

        // When & Then
        StepVerifier.create(loanApplicationHandler.obtainSummary())
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO001.getMessage(), response.getResponseMessage());
                    assertEquals(loanStatsDTO, response.getData());
                })
                .verifyComplete();

        verify(obtainSumaryApplicationUseCase, times(1)).execute();
        verify(loanApplicationApiRestMapper, times(1)).loanStatsToLoanStatsDTO(loanStats);
    }

    @Test
    void shouldHandleInternalErrorExceptionInObtainSummary() {
        // Given
        InternalErrorException exception = new InternalErrorException(ResponseCode.MRPO000);
        when(obtainSumaryApplicationUseCase.execute())
                .thenReturn(Mono.error(exception));

        // When & Then
        StepVerifier.create(loanApplicationHandler.obtainSummary())
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO000.getMessage(), response.getResponseMessage());
                })
                .verifyComplete();

        verify(obtainSumaryApplicationUseCase, times(1)).execute();
        verify(loanApplicationApiRestMapper, never()).loanStatsToLoanStatsDTO(any());
    }

    @Test
    void shouldHandleNotFoundExceptionInObtainSummary() {
        // Given
        NotFoundException exception = new NotFoundException(ResponseCode.MRPO004);
        when(obtainSumaryApplicationUseCase.execute())
                .thenReturn(Mono.error(exception));

        // When & Then
        StepVerifier.create(loanApplicationHandler.obtainSummary())
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO004.getMessage(), response.getResponseMessage());
                })
                .verifyComplete();

        verify(obtainSumaryApplicationUseCase, times(1)).execute();
    }

    @Test
    void shouldHandleGenericExceptionInObtainSummary() {
        // Given
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(obtainSumaryApplicationUseCase.execute())
                .thenReturn(Mono.error(exception));

        // When & Then
        StepVerifier.create(loanApplicationHandler.obtainSummary())
                .expectError(RuntimeException.class)
                .verify();

        verify(obtainSumaryApplicationUseCase, times(1)).execute();
    }

    @Test
    void shouldHandleEmptyStatsSuccessfully() {
        // Given
        LoanStats emptyStats = new LoanStats(0, 0.0);
        LoanStatsDTO emptyStatsDTO = new LoanStatsDTO(0, 0.0);

        when(obtainSumaryApplicationUseCase.execute())
                .thenReturn(Mono.just(emptyStats));
        when(loanApplicationApiRestMapper.loanStatsToLoanStatsDTO(emptyStats))
                .thenReturn(emptyStatsDTO);

        // When & Then
        StepVerifier.create(loanApplicationHandler.obtainSummary())
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK.value(), response.getResponseCode());
                    assertEquals(ResponseCode.MRPO001.getMessage(), response.getResponseMessage());
                    assertEquals(emptyStatsDTO, response.getData());
                })
                .verifyComplete();
    }
}
