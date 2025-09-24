package co.com.pragma.usecase;

import co.com.pragma.model.loan_application.LoanStats;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtainSumaryApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private ObtainSumaryApplicationUseCase obtainSumaryUseCase;

    @BeforeEach
    void setUp() {
        obtainSumaryUseCase = new ObtainSumaryApplicationUseCase(loanApplicationRepository);
    }

    @Test
    void shouldObtainLoanStatsSuccessfully() {
        // Given
        LoanStats expectedStats = new LoanStats(10, 500000.0);

        when(loanApplicationRepository.getBySummary())
                .thenReturn(Mono.just(expectedStats));

        // When & Then
        StepVerifier.create(obtainSumaryUseCase.execute())
                .expectNext(expectedStats)
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).getBySummary();
    }

    @Test
    void shouldHandleEmptyStats() {
        // Given
        LoanStats emptyStats = new LoanStats(0, 0.0);

        when(loanApplicationRepository.getBySummary())
                .thenReturn(Mono.just(emptyStats));

        // When & Then
        StepVerifier.create(obtainSumaryUseCase.execute())
                .expectNext(emptyStats)
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).getBySummary();
    }

    @Test
    void shouldHandleRepositoryError() {
        // Given
        RuntimeException repositoryError = new RuntimeException("Error obteniendo estadísticas");

        when(loanApplicationRepository.getBySummary())
                .thenReturn(Mono.error(repositoryError));

        // When & Then
        StepVerifier.create(obtainSumaryUseCase.execute())
                .expectError(RuntimeException.class)
                .verify();

        verify(loanApplicationRepository, times(1)).getBySummary();
    }

    @Test
    void shouldHandleLargeNumbers() {
        // Given
        LoanStats largeStats = new LoanStats(1000000, 99999999999.99);

        when(loanApplicationRepository.getBySummary())
                .thenReturn(Mono.just(largeStats));

        // When & Then
        StepVerifier.create(obtainSumaryUseCase.execute())
                .expectNext(largeStats)
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).getBySummary();
    }

    @Test
    void shouldHandleRepositoryTimeout() {
        // Given
        when(loanApplicationRepository.getBySummary())
                .thenReturn(Mono.never()); // Simula un timeout

        // When & Then
        StepVerifier.create(obtainSumaryUseCase.execute())
                .expectTimeout(java.time.Duration.ofSeconds(1))
                .verify();

        verify(loanApplicationRepository, times(1)).getBySummary();
    }
}
