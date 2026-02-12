package dev.aira.lambda.feedback.infrastructure;

import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.exceptions.ErroSQSException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import static io.quarkus.amazon.lambda.runtime.AmazonLambdaMapperRecorder.objectMapper;

@ApplicationScoped
public class FeedbackSQSProducer {

    private final SqsClient sqsClient;

    public FeedbackSQSProducer(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @ConfigProperty(name = "aws.sqs.feedback-url")
    String queueUrl;

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
