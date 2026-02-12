package dev.aira.lambda.feedback.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.exceptions.ErroSNSException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@ApplicationScoped
public class FeedbackSNSProducer {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Inject
    public FeedbackSNSProducer(SnsClient snsClient, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    @ConfigProperty(name = "aws.sns.feedback-topic-arn")
    String topicArn;

    public void enviarAlerta(FeedbackMessage message) {
        try {
            String body = objectMapper.writeValueAsString(message);

            snsClient.publish(
                    PublishRequest.builder()
                            .topicArn(topicArn)
                            .message(body)
                            .build()
            );
        } catch (Exception e) {
            throw new ErroSNSException(e);
        }
    }

}
