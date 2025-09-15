package co.com.pragma.sqs.listener.dto;

import co.com.pragma.model.loan_application.LoanApplicationStatus;
import co.com.pragma.model.loan_application.LoanType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanApplicationDTO {
    private Long id;
    private Double amount;
    private Integer term;
    private String email;
    private Long loanApplicationStatusId;
    private LoanApplicationStatus status;
    private Long loanTypeId;
    private LoanType loanType;
}
