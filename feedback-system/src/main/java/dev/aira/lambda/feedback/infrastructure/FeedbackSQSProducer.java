package dev.aira.lambda.feedback.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.exceptions.ErroSQSException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@ApplicationScoped
public class FeedbackSQSProducer {

    @ConfigProperty(name = "aws.sqs.feedback-url")
    String queueUrl;

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Inject
    public FeedbackSQSProducer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    public void enviar(FeedbackMessage message) {
        try {
            String body = objectMapper.writeValueAsString(message);
            sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(body)
                            .build()
            );
        } catch (Exception e) {
            throw new ErroSQSException(e);
        }
    }
}
