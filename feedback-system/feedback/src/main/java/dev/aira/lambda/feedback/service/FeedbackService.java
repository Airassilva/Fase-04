package dev.aira.lambda.feedback.service;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.dto.FeedbackRequest;
import dev.aira.lambda.feedback.dto.FeedbackResponse;
import dev.aira.lambda.feedback.mapper.FeedbackMapper;
import dev.aira.lambda.feedback.repository.FeedbackRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


@ApplicationScoped
public class FeedbackService {

    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);

    private final FeedbackRepository feedbackRepository;
    private final FilaFeedbackService filaFeedbackService;
    private final AlertFeedbackService alertFeedbackService;

    @Inject
    public FeedbackService(FeedbackRepository feedbackRepository, FilaFeedbackService filaFeedbackService, AlertFeedbackService alertFeedbackService) {
        this.feedbackRepository = feedbackRepository;
        this.filaFeedbackService = filaFeedbackService;
        this.alertFeedbackService = alertFeedbackService;
    }

    public FeedbackResponse executar(FeedbackRequest request) {
        Feedback feedback = new Feedback(
                UUID.randomUUID().toString(),
                request.nota,
                request.descricao
        );
        feedbackRepository.save(feedback);
        log.info("Feedback salvo com feedbackId={}", feedback.getId());
        filaFeedbackService.enviarSQS(feedback);

        if (feedback.isUrgenciaAlta()) {
            log.info("Enviando alerta urgente com feedbackId={}", feedback.getId());
            alertFeedbackService.enviarSNS(feedback);
        }

        return FeedbackMapper.toResponse(feedback);
    }
}
