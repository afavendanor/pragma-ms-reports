package co.com.pragma.sqs.listener.handler;

import co.com.pragma.sqs.listener.dto.LoanApplicationDTO;
import co.com.pragma.sqs.listener.mapper.LoanApplicationSQSMapper;
import co.com.pragma.usecase.SaveLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanApplicationSQSHandler {

    private final SaveLoanApplicationUseCase saveLoanApplicationUseCase;
    private final LoanApplicationSQSMapper mapper;

    public Mono<Void> process(LoanApplicationDTO dto) {
        return Mono.just(dto)
                .map(mapper::loanApplicationDTOToLoanApplication)
                .flatMap(saveLoanApplicationUseCase::execute)
                .then();
    }
}
