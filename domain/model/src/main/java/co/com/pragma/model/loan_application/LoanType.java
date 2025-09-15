package co.com.pragma.model.loan_application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanType {
    private Long id;
    private String name;
    private Double minimumAmount;
    private Double maximumAmount;
    private Double interestRate;
    private Boolean automaticValidation;
}
