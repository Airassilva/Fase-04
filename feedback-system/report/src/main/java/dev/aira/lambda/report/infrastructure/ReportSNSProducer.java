package dev.aira.lambda.report.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@ApplicationScoped
public class ReportSNSProducer {

    @ConfigProperty(name = "aws.sns.feedback-topic-arn")
    String topicArn;

    private final SnsClient snsClient;

    @Inject
    public ReportSNSProducer(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void enviarAlerta(String linkS3) {
        String body = String.format("""
                🚨 RELATÓRIO SEMANAL DE FEEDBACK 🚨
                
                Você pode ver o relatório disponibilizado no link abaixo,
                nele você encontrará as métricas de todas as avaliações!
                
                Link: %s
                """, linkS3
        );

        snsClient.publish(
                PublishRequest.builder()
                        .topicArn(topicArn)
                        .subject("Relatório Semanal das avaliações!")
                        .message(body)
                        .build()
        );
    }

}
