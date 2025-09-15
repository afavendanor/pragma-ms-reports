package co.com.pragma.api.mapper;


import co.com.pragma.api.dto.LoanStatsDTO;
import co.com.pragma.model.loan_application.LoanStats;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationApiRestMapper {

    LoanStatsDTO loanStatsToLoanStatsDTO(LoanStats loanStats);

}
