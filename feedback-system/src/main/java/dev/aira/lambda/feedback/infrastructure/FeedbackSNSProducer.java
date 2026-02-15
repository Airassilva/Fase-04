package dev.aira.lambda.feedback.infrastructure;

import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.exceptions.ErroSNSException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@ApplicationScoped
public class FeedbackSNSProducer {

    @ConfigProperty(name = "aws.sns.feedback-topic-arn")
    String topicArn;

    private final SnsClient snsClient;

    @Inject
    public FeedbackSNSProducer(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void enviarAlerta(FeedbackMessage message) {
        try {
            String body = String.format("""
                    🚨 ALERTA DE FEEDBACK URGENTE 🚨

                    📋 Descrição: %s
                    ⚠️  Urgência: %s
                    📅 Data de Envio: %s
                    """,
                    message.getDescricao(),
                    message.getUrgencia(),
                    message.getDataEnvio()
            );

            snsClient.publish(
                    PublishRequest.builder()
                            .topicArn(topicArn)
                            .subject("🚨 Feedback Urgente - " + message.getUrgencia())
                            .message(body)
                            .build()
            );
        } catch (Exception e) {
            throw new ErroSNSException(e);
        }
    }

}
