package co.com.pragma.usecase;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private SaveLoanApplicationUseCase saveUseCase;

    @BeforeEach
    void setUp() {
        saveUseCase = new SaveLoanApplicationUseCase(loanApplicationRepository);
    }

    @Test
    void shouldSaveLoanApplicationSuccessfully() {
        // Given
        LoanApplication loanApplication = LoanApplication.builder()
                .id(1L)
                .amount(50000.0)
                .term(12)
                .email("test@example.com")
                .loanApplicationStatusId(1L)
                .loanTypeId(1L)
                .build();

        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(saveUseCase.execute(loanApplication))
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(loanApplication);
    }

    @Test
    void shouldHandleRepositoryError() {
        // Given
        LoanApplication loanApplication = LoanApplication.builder()
                .id(1L)
                .amount(50000.0)
                .term(12)
                .email("test@example.com")
                .build();

        RuntimeException repositoryError = new RuntimeException("Error guardando en base de datos");
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(repositoryError));

        // When & Then
        StepVerifier.create(saveUseCase.execute(loanApplication))
                .expectError(RuntimeException.class)
                .verify();

        verify(loanApplicationRepository, times(1)).save(loanApplication);
    }

    @Test
    void shouldHandleNullLoanApplication() {
        // Given
        when(loanApplicationRepository.save(null))
                .thenReturn(Mono.error(new IllegalArgumentException("LoanApplication no puede ser null")));

        // When & Then
        StepVerifier.create(saveUseCase.execute(null))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(loanApplicationRepository, times(1)).save(null);
    }

    @Test
    void shouldSaveLoanApplicationWithMinimalData() {
        // Given
        LoanApplication minimalLoanApplication = LoanApplication.builder()
                .amount(1000.0)
                .email("minimal@test.com")
                .build();

        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(saveUseCase.execute(minimalLoanApplication))
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(minimalLoanApplication);
    }
}
