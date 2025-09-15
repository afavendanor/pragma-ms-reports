package co.com.pragma.sqs.listener.mapper;


import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.sqs.listener.dto.LoanApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationSQSMapper {

    LoanApplication loanApplicationDTOToLoanApplication(LoanApplicationDTO loanApplicationDTO);

}
