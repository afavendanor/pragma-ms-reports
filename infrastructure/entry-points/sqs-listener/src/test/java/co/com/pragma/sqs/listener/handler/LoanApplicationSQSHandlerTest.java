package co.com.pragma.sqs.listener.handler;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.sqs.listener.dto.LoanApplicationDTO;
import co.com.pragma.sqs.listener.mapper.LoanApplicationSQSMapper;
import co.com.pragma.usecase.SaveLoanApplicationUseCase;
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
class LoanApplicationSQSHandlerTest {

    @Mock
    private SaveLoanApplicationUseCase saveLoanApplicationUseCase;

    @Mock
    private LoanApplicationSQSMapper mapper;

    private LoanApplicationSQSHandler handler;

    @BeforeEach
    void setUp() {
        handler = new LoanApplicationSQSHandler(saveLoanApplicationUseCase, mapper);
    }

    @Test
    void shouldProcessLoanApplicationDTOSuccessfully() {
        // Given
        LoanApplicationDTO dto = createTestLoanApplicationDTO();
        LoanApplication loanApplication = createTestLoanApplication();

        when(mapper.loanApplicationDTOToLoanApplication(dto))
                .thenReturn(loanApplication);
        when(saveLoanApplicationUseCase.execute(loanApplication))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(handler.process(dto))
                .verifyComplete();

        verify(mapper, times(1)).loanApplicationDTOToLoanApplication(dto);
        verify(saveLoanApplicationUseCase, times(1)).execute(loanApplication);
    }

    @Test
    void shouldHandleMapperError() {
        // Given
        LoanApplicationDTO dto = createTestLoanApplicationDTO();
        RuntimeException mapperError = new RuntimeException("Mapping error");

        when(mapper.loanApplicationDTOToLoanApplication(dto))
                .thenThrow(mapperError);

        // When & Then
        StepVerifier.create(handler.process(dto))
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper, times(1)).loanApplicationDTOToLoanApplication(dto);
        verify(saveLoanApplicationUseCase, never()).execute(any());
    }

    @Test
    void shouldHandleUseCaseError() {
        // Given
        LoanApplicationDTO dto = createTestLoanApplicationDTO();
        LoanApplication loanApplication = createTestLoanApplication();
        RuntimeException useCaseError = new RuntimeException("UseCase error");

        when(mapper.loanApplicationDTOToLoanApplication(dto))
                .thenReturn(loanApplication);
        when(saveLoanApplicationUseCase.execute(loanApplication))
                .thenReturn(Mono.error(useCaseError));

        // When & Then
        StepVerifier.create(handler.process(dto))
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper, times(1)).loanApplicationDTOToLoanApplication(dto);
        verify(saveLoanApplicationUseCase, times(1)).execute(loanApplication);
    }

    @Test
    void shouldProcessNullFieldsInDTO() {
        // Given
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(1L);
        dto.setAmount(null);
        dto.setTerm(null);

        LoanApplication loanApplication = LoanApplication.builder()
                .id(1L)
                .amount(null)
                .term(null)
                .build();

        when(mapper.loanApplicationDTOToLoanApplication(dto))
                .thenReturn(loanApplication);
        when(saveLoanApplicationUseCase.execute(loanApplication))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(handler.process(dto))
                .verifyComplete();

        verify(mapper, times(1)).loanApplicationDTOToLoanApplication(dto);
        verify(saveLoanApplicationUseCase, times(1)).execute(loanApplication);
    }

    private LoanApplicationDTO createTestLoanApplicationDTO() {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(1L);
        dto.setAmount(50000.0);
        dto.setTerm(12);
        dto.setEmail("test@example.com");
        dto.setLoanTypeId(1L);
        return dto;
    }

    private LoanApplication createTestLoanApplication() {
        return LoanApplication.builder()
                .id(1L)
                .amount(50000.0)
                .term(12)
                .email("test@example.com")
                .loanTypeId(1L)
                .build();
    }
}
