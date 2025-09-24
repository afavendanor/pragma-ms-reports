package co.com.pragma.sqs.listener;


import co.com.pragma.sqs.listener.config.SQSProperties;
import co.com.pragma.sqs.listener.dto.LoanApplicationDTO;
import co.com.pragma.sqs.listener.dto.SnsEnvelopeDTO;
import co.com.pragma.sqs.listener.handler.LoanApplicationSQSHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class LoanApplicationListener {

    private static final Logger log = Loggers.getLogger(LoanApplicationListener.class.getName());

    private final LoanApplicationSQSHandler handler;
    private final SQSProperties properties;
    private final SqsAsyncClient sqsClient;
    private final ObjectMapper objectMapper;

    public Disposable startListening() {
        return Flux.defer(() -> Mono.fromFuture(() ->
                                sqsClient.receiveMessage(
                                        ReceiveMessageRequest.builder()
                                                .queueUrl(properties.queueUrl())
                                                .maxNumberOfMessages(5)
                                                .waitTimeSeconds(10) // long polling
                                                .build()
                                )
                        )
                        .flatMapMany(resp -> Flux.fromIterable(resp.messages())))
                .repeat()
                .doOnSubscribe(s -> log.info("▶️ Iniciando listener SQS..."))
                .doOnNext(msg -> log.info("📩 Mensaje recibido: " + msg.body()))
                .flatMap(this::delegateToHandler)
                .onErrorContinue((ex, obj) ->
                        log.info("❌ Error procesando mensaje: " + ex.getMessage()))
                .subscribe();
    }

    Mono<Void> delegateToHandler(Message message) {
        try {
            SnsEnvelopeDTO envelope = objectMapper.readValue(message.body(), SnsEnvelopeDTO.class);
            String innerMessage = envelope.getMessage();
            LoanApplicationDTO dto = objectMapper.readValue(innerMessage, LoanApplicationDTO.class);
            return handler.process(dto)
                    .then(deleteMessage(message));
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("❌ Error deserializando LoanApplicationDTO", e));
        }
    }

    Mono<Void> deleteMessage(Message message) {
        return Mono.fromFuture(() ->
                        sqsClient.deleteMessage(b -> b.queueUrl(properties.queueUrl())
                                .receiptHandle(message.receiptHandle())))
                .doOnSuccess(v -> log.error("🗑️ Mensaje eliminado de la cola"))
                .then();
    }
}