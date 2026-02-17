package dev.aira.lambda.ut;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.exceptions.ErroSQSException;
import dev.aira.lambda.feedback.infrastructure.FeedbackSNSProducer;
import dev.aira.lambda.feedback.service.AlertFeedbackService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class AlertFeedbackServiceTest {

    @Inject
    AlertFeedbackService service;

    @InjectMock
    FeedbackSNSProducer feedbackSNSProducer;

    @Test
    void enviarMensagemAlerta(){
        Feedback feedback = new Feedback(UUID.randomUUID().toString(), 3, "Aula ruim!");

        service.enviarSNS(feedback);

        verify(feedbackSNSProducer, times(1)).enviarAlerta(any(FeedbackMessage.class));
    }

    @Test
    void erroAoEnviarAlerta(){
        Feedback feedback = new Feedback(UUID.randomUUID().toString(), 3, "Aula ruim!");
        doThrow(ErroSQSException.class).when(feedbackSNSProducer).enviarAlerta(any(FeedbackMessage.class));

        assertThrows(ErroSQSException.class,() -> service.enviarSNS(feedback));

        verify(feedbackSNSProducer, times(1)).enviarAlerta(any(FeedbackMessage.class));
    }
}
