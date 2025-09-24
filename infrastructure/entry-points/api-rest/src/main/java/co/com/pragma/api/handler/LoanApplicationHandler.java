package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.api.dto.LoanStatsDTO;
import co.com.pragma.api.mapper.LoanApplicationApiRestMapper;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.usecase.ObtainSumaryApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private static final Logger log = Loggers.getLogger(LoanApplicationHandler.class.getName());

    private final ObtainSumaryApplicationUseCase obtainSumaryApplicationUseCase;
    private final LoanApplicationApiRestMapper loanApplicationApiRestMapper;

    public Mono<GenericResponseDTO<LoanStatsDTO>> obtainSummary() {

        ErrorHandler<LoanStatsDTO> errorHandler = new ErrorHandler<>();
        return errorHandler.addErrors(
                Mono.defer(() -> {
                    log.debug("Inicializar consulta de resumen");
                    return obtainSumaryApplicationUseCase.execute()
                            .map(loanApplicationApiRestMapper::loanStatsToLoanStatsDTO)
                            .map(dto -> new GenericResponseDTO<>(HttpStatus.OK, ResponseCode.MRPO001, dto))
                            .doOnSuccess(response -> log.debug("Finalizar consulta de resumen"));
                }),
                "obtainSummary");
    }

}