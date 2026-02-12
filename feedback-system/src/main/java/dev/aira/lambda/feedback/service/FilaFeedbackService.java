package dev.aira.lambda.feedback.service;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.infrastructure.FeedbackSQSProducer;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class FilaFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(FilaFeedbackService.class);

    private final FeedbackSQSProducer  feedbackSQSProducer;

    public FilaFeedbackService(FeedbackSQSProducer feedbackSQSProducer) {
        this.feedbackSQSProducer = feedbackSQSProducer;
    }

    public void enviarSQS(Feedback feedback) {
            var message = new FeedbackMessage(feedback);
            log.info("Enviando feedback SQS message={}", message);
            feedbackSQSProducer.enviar(message);
    }
}
