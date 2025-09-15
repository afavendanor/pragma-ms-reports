package co.com.pragma.usecase;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SaveLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    public Mono<Void> execute(LoanApplication loanApplication) {
        return loanApplicationRepository.save(loanApplication);
    }

}
