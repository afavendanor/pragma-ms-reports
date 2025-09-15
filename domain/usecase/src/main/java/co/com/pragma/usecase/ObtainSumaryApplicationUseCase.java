package co.com.pragma.usecase;

import co.com.pragma.model.loan_application.LoanStats;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ObtainSumaryApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    public Mono<LoanStats> execute() {
        return loanApplicationRepository.getBySummary();
    }

}
