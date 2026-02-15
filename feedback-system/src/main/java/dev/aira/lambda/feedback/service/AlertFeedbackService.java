package dev.aira.lambda.feedback.service;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.infrastructure.FeedbackSNSProducer;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class AlertFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(AlertFeedbackService.class);

    private final FeedbackSNSProducer feedbackSNSProducer;

    public AlertFeedbackService(FeedbackSNSProducer feedbackSNSProducer) {
        this.feedbackSNSProducer = feedbackSNSProducer;
    }

    public void enviarSNS(Feedback feedback) {
            var message = new FeedbackMessage(feedback);
            log.info("Enviando feedback SNS Urgente feedbackDescricao={}", message.getDescricao());
            feedbackSNSProducer.enviarAlerta(message);
    }
}
