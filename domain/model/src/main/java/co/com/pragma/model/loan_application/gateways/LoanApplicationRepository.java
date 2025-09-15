package co.com.pragma.model.loan_application.gateways;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.LoanStats;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {

    Mono<Void> save(LoanApplication loanApplication);

    Mono<LoanStats> getBySummary();

}
