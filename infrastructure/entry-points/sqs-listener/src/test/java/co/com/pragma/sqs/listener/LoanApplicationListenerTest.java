package co.com.pragma.sqs.listener;

import co.com.pragma.sqs.listener.config.SQSProperties;
import co.com.pragma.sqs.listener.dto.LoanApplicationDTO;
import co.com.pragma.sqs.listener.dto.SnsEnvelopeDTO;
import co.com.pragma.sqs.listener.handler.LoanApplicationSQSHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationListenerTest {

    @Mock
    private LoanApplicationSQSHandler handler;

    @Mock
    private SQSProperties properties;

    @Mock
    private SqsAsyncClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    private LoanApplicationListener listener;

    @BeforeEach
    void setUp() {
        listener = new LoanApplicationListener(handler, properties, sqsClient, objectMapper);
        lenient().when(properties.queueUrl()).thenReturn("https://sqs.us-east-1.amazonaws.com/123456789/test-queue");
    }


    @Test
    void delegateToHandler_shouldDeserializeAndProcessSuccessfully() throws Exception {
        // Given
        String messageBody = "{\"Message\": \"{\\\"id\\\": 1, \\\"amount\\\": 50000.0}\"}";
        String innerMessage = "{\"id\": 1, \"amount\": 50000.0}";

        Message message = Message.builder()
                .body(messageBody)
                .receiptHandle("test-receipt-handle")
                .build();

        SnsEnvelopeDTO envelope = new SnsEnvelopeDTO();
        envelope.setMessage(innerMessage);

        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(1L);
        dto.setAmount(50000.0);

        when(objectMapper.readValue(messageBody, SnsEnvelopeDTO.class))
                .thenReturn(envelope);
        when(objectMapper.readValue(innerMessage, LoanApplicationDTO.class))
                .thenReturn(dto);
        when(handler.process(dto))
                .thenReturn(Mono.empty());
        when(sqsClient.deleteMessage(any(Consumer.class)))
                .thenReturn(CompletableFuture.completedFuture(DeleteMessageResponse.builder().build()));

        // When & Then
        StepVerifier.create(listener.delegateToHandler(message))
                .verifyComplete();

        verify(handler, times(1)).process(dto);
        verify(sqsClient, times(1)).deleteMessage(any(Consumer.class));
    }

    @Test
    void delegateToHandler_shouldHandleJsonProcessingException() throws Exception {
        // Given
        String invalidMessageBody = "invalid json";

        Message message = Message.builder()
                .body(invalidMessageBody)
                .receiptHandle("test-receipt-handle")
                .build();

        when(objectMapper.readValue(invalidMessageBody, SnsEnvelopeDTO.class))
                .thenThrow(new JsonProcessingException("JSON parsing error") {});

        // When & Then
        StepVerifier.create(listener.delegateToHandler(message))
                .expectError(RuntimeException.class)
                .verify();

        verify(handler, never()).process(any(LoanApplicationDTO.class));
        verify(sqsClient, never()).deleteMessage(any(Consumer.class));
    }

    @Test
    void delegateToHandler_shouldHandleHandlerError() throws Exception {
        // Given
        String messageBody = "{\"Message\": \"{\\\"id\\\": 1, \\\"amount\\\": 50000.0}\"}";
        String innerMessage = "{\"id\": 1, \"amount\": 50000.0}";

        Message message = Message.builder()
                .body(messageBody)
                .receiptHandle("test-receipt-handle")
                .build();

        SnsEnvelopeDTO envelope = new SnsEnvelopeDTO();
        envelope.setMessage(innerMessage);

        LoanApplicationDTO dto = new LoanApplicationDTO();

        when(objectMapper.readValue(messageBody, SnsEnvelopeDTO.class))
                .thenReturn(envelope);
        when(objectMapper.readValue(innerMessage, LoanApplicationDTO.class))
                .thenReturn(dto);
        when(handler.process(dto))
                .thenReturn(Mono.error(new RuntimeException("Handler error")));

        // When & Then
        StepVerifier.create(listener.delegateToHandler(message))
                .expectError(RuntimeException.class)
                .verify();

        verify(handler, times(1)).process(dto);
        verify(sqsClient, never()).deleteMessage(any(Consumer.class));
    }

    @Test
    void deleteMessage_shouldCallSqsClientSuccessfully() {
        // Given
        Message message = Message.builder()
                .body("dummy")
                .receiptHandle("test-receipt-handle")
                .build();

        when(sqsClient.deleteMessage(any(Consumer.class)))
                .thenReturn(CompletableFuture.completedFuture(DeleteMessageResponse.builder().build()));

        // When & Then
        StepVerifier.create(listener.deleteMessage(message))
                .verifyComplete();

        verify(sqsClient, times(1)).deleteMessage(any(Consumer.class));
    }

    @Test
    void deleteMessage_shouldHandleSqsError() {
        // Given
        Message message = Message.builder()
                .body("dummy")
                .receiptHandle("test-receipt-handle")
                .build();

        CompletableFuture<DeleteMessageResponse> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("SQS error"));

        when(sqsClient.deleteMessage(any(Consumer.class)))
                .thenReturn(failedFuture);

        // When & Then
        StepVerifier.create(listener.deleteMessage(message))
                .expectError(RuntimeException.class)
                .verify();

        verify(sqsClient, times(1)).deleteMessage(any(Consumer.class));
    }
}
