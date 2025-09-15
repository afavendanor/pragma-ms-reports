package co.com.pragma.sqs.listener.config;

import co.com.pragma.sqs.listener.LoanApplicationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ListenerConfig {

    private final LoanApplicationListener listener;

    @Bean
    public ApplicationRunner runner() {
        return args -> listener.startListening();
    }
}
