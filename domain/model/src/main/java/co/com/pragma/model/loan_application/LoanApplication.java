package co.com.pragma.model.loan_application;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private Long id;
    private Double amount;
    private Integer term;
    private String email;
    private Long loanApplicationStatusId;
    private LoanApplicationStatus status;
    private Long loanTypeId;
    private LoanType loanType;
}
