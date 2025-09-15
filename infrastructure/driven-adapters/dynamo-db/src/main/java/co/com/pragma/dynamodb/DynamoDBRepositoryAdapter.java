package co.com.pragma.dynamodb;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.LoanStats;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class DynamoDBRepositoryAdapter implements LoanApplicationRepository {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    @Override
    public Mono<Void> save(LoanApplication loanApp) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("loanApplicationId", AttributeValue.builder().s(loanApp.getId().toString()).build());
        item.put("amount", AttributeValue.builder().n(loanApp.getAmount().toString()).build());
        item.put("term", AttributeValue.builder().n(String.valueOf(loanApp.getTerm())).build());
        item.put("email", AttributeValue.builder().s(loanApp.getEmail()).build());
        item.put("status", AttributeValue.builder().s(LoanApplicationStatus.APPROVED.name()).build());
        item.put("loanTypeId", AttributeValue.builder().n(String.valueOf(loanApp.getLoanTypeId())).build());
        item.put("approvedAt", AttributeValue.builder().s(Instant.now().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("ApprovedLoanApplications")
                .item(item)
                .build();

        return Mono.fromFuture(() -> dynamoDbAsyncClient.putItem(request))
                .then();
    }

    @Override
    public Mono<LoanStats> getBySummary() {
        ScanRequest request = ScanRequest.builder()
                .tableName("ApprovedLoanApplications")
                .build();

        return Mono.fromFuture(() -> dynamoDbAsyncClient.scan(request))
                .map(response -> {
                    int totalSolicitudes = response.count();
                    double montoTotal = response.items().stream()
                            .mapToDouble(item -> Double.parseDouble(item.get("amount").n()))
                            .sum();
                    return new LoanStats(totalSolicitudes, montoTotal);
                });
    }
}
