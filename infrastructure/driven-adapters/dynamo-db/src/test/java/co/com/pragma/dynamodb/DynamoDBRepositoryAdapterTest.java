package co.com.pragma.dynamodb;

import co.com.pragma.model.loan_application.LoanApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamoDBRepositoryAdapterTest {

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    private DynamoDBRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new DynamoDBRepositoryAdapter(dynamoDbAsyncClient);
    }

    @Test
    void shouldSaveLoanApplicationSuccessfully() {
        // Given
        LoanApplication loanApplication = createTestLoanApplication();
        PutItemResponse response = PutItemResponse.builder().build();

        when(dynamoDbAsyncClient.putItem(any(PutItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // When & Then
        StepVerifier.create(repositoryAdapter.save(loanApplication))
                .verifyComplete();

        verify(dynamoDbAsyncClient, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    void shouldHandleSaveError() {
        // Given
        LoanApplication loanApplication = createTestLoanApplication();
        RuntimeException error = new RuntimeException("DynamoDB error");
        CompletableFuture<PutItemResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(error);

        when(dynamoDbAsyncClient.putItem(any(PutItemRequest.class)))
                .thenReturn(failedFuture);

        // When & Then
        StepVerifier.create(repositoryAdapter.save(loanApplication))
                .expectError(RuntimeException.class)
                .verify();

        verify(dynamoDbAsyncClient, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    void shouldGetSummarySuccessfully() {
        // Given
        Map<String, AttributeValue> item1 = Map.of(
                "amount", AttributeValue.builder().n("50000.0").build()
        );
        Map<String, AttributeValue> item2 = Map.of(
                "amount", AttributeValue.builder().n("75000.0").build()
        );

        ScanResponse scanResponse = ScanResponse.builder()
                .items(List.of(item1, item2))
                .count(2)
                .build();

        when(dynamoDbAsyncClient.scan(any(ScanRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(scanResponse));

        // When & Then
        StepVerifier.create(repositoryAdapter.getBySummary())
                .assertNext(stats -> {
                    assertEquals(2, stats.totalSolicitudes());
                    assertEquals(125000.0, stats.montoTotal());
                })
                .verifyComplete();

        verify(dynamoDbAsyncClient, times(1)).scan(any(ScanRequest.class));
    }

    @Test
    void shouldHandleEmptySummary() {
        // Given
        ScanResponse scanResponse = ScanResponse.builder()
                .items(List.of())
                .count(0)
                .build();

        when(dynamoDbAsyncClient.scan(any(ScanRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(scanResponse));

        // When & Then
        StepVerifier.create(repositoryAdapter.getBySummary())
                .assertNext(stats -> {
                    assertEquals(0, stats.totalSolicitudes());
                    assertEquals(0.0, stats.montoTotal());
                })
                .verifyComplete();

        verify(dynamoDbAsyncClient, times(1)).scan(any(ScanRequest.class));
    }

    @Test
    void shouldHandleScanError() {
        // Given
        RuntimeException error = new RuntimeException("DynamoDB scan error");
        CompletableFuture<ScanResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(error);

        when(dynamoDbAsyncClient.scan(any(ScanRequest.class)))
                .thenReturn(failedFuture);

        // When & Then
        StepVerifier.create(repositoryAdapter.getBySummary())
                .expectError(RuntimeException.class)
                .verify();

        verify(dynamoDbAsyncClient, times(1)).scan(any(ScanRequest.class));
    }

    @Test
    void shouldHandleSingleItemSummary() {
        // Given
        Map<String, AttributeValue> singleItem = Map.of(
                "amount", AttributeValue.builder().n("100000.0").build()
        );

        ScanResponse scanResponse = ScanResponse.builder()
                .items(List.of(singleItem))
                .count(1)
                .build();

        when(dynamoDbAsyncClient.scan(any(ScanRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(scanResponse));

        // When & Then
        StepVerifier.create(repositoryAdapter.getBySummary())
                .assertNext(stats -> {
                    assertEquals(1, stats.totalSolicitudes());
                    assertEquals(100000.0, stats.montoTotal());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleLargeAmountsSummary() {
        // Given
        Map<String, AttributeValue> item1 = Map.of(
                "amount", AttributeValue.builder().n("999999.99").build()
        );
        Map<String, AttributeValue> item2 = Map.of(
                "amount", AttributeValue.builder().n("1000000.01").build()
        );

        ScanResponse scanResponse = ScanResponse.builder()
                .items(List.of(item1, item2))
                .count(2)
                .build();

        when(dynamoDbAsyncClient.scan(any(ScanRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(scanResponse));

        // When & Then
        StepVerifier.create(repositoryAdapter.getBySummary())
                .assertNext(stats -> {
                    assertEquals(2, stats.totalSolicitudes());
                    assertEquals(2000000.0, stats.montoTotal(), 0.01);
                })
                .verifyComplete();
    }

    private LoanApplication createTestLoanApplication() {
        return LoanApplication.builder()
                .id(1L)
                .amount(50000.0)
                .term(12)
                .email("test@example.com")
                .loanTypeId(1L)
                .build();
    }
}
