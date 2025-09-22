package co.com.crediya.sqs.config;


import org.springframework.beans.factory.annotation.Value;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SqsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        log.info("Configurando SQS Client para región: {}", region);
        return SqsAsyncClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory() {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient())
                .build();
    }


}
