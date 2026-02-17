package dev.aira.lambda.ut;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.dto.FeedbackMessage;
import dev.aira.lambda.feedback.exceptions.ErroSQSException;
import dev.aira.lambda.feedback.infrastructure.FeedbackSQSProducer;
import dev.aira.lambda.feedback.service.FilaFeedbackService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
class FilaFeedbackServiceTest {
    @Inject
    FilaFeedbackService filaFeedbackService;

    @InjectMock
    FeedbackSQSProducer feedbackSQSProducer;

    @Test
    void enviarMensagemFilaSQS(){
        Feedback feedback = new Feedback(UUID.randomUUID().toString(), 5, "Boa aula!");

        filaFeedbackService.enviarSQS(feedback);

        verify(feedbackSQSProducer, times(1)).enviar(any(FeedbackMessage.class));
    }

    @Test
    void erroEnviarMensagemFilaSQS(){
        Feedback feedback = new Feedback(UUID.randomUUID().toString(), 5, "Boa aula!");
        doThrow(ErroSQSException.class).when(feedbackSQSProducer).enviar(any(FeedbackMessage.class));

        assertThrows(ErroSQSException.class,() -> filaFeedbackService.enviarSQS(feedback));

        verify(feedbackSQSProducer, times(1)).enviar(any(FeedbackMessage.class));
    }
}
