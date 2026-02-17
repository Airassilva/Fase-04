package dev.aira.lambda.ut;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.domain.Urgencia;
import dev.aira.lambda.feedback.dto.FeedbackRequest;
import dev.aira.lambda.feedback.exceptions.ErroSNSException;
import dev.aira.lambda.feedback.exceptions.ErroSQSException;
import dev.aira.lambda.feedback.exceptions.PersistenceException;
import dev.aira.lambda.feedback.repository.FeedbackRepository;
import dev.aira.lambda.feedback.service.AlertFeedbackService;
import dev.aira.lambda.feedback.service.FeedbackService;
import dev.aira.lambda.feedback.service.FilaFeedbackService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class FeedbackServiceTest {

    @Inject
    FeedbackService service;

    @InjectMock
    FeedbackRepository feedbackRepository;

    @InjectMock
    FilaFeedbackService filaFeedbackService;

    @InjectMock
    AlertFeedbackService alertFeedbackService;

    @BeforeEach
    void setUp() {
        reset(feedbackRepository, filaFeedbackService, alertFeedbackService);
    }

    @Test
    void criarAvaliacaoUrgenciaAlta() {
        FeedbackRequest request = new FeedbackRequest(3, "Aula péssima!");

        var response =  service.executar(request);

        assertEquals(Urgencia.ALTA, response.getUrgencia());
        assertEquals("Aula péssima!", response.getDescricao());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(filaFeedbackService, times(1)).enviarSQS(any(Feedback.class));
        verify(alertFeedbackService, times(1)).enviarSNS(any(Feedback.class));
    }

    @Test
    void criarAvaliacaoUrgenciaMedia() {
        FeedbackRequest request = new FeedbackRequest(6, "Aula boa, mas pode melhorar!");

        var response =  service.executar(request);

        assertEquals(Urgencia.MEDIA, response.getUrgencia());
        assertEquals("Aula boa, mas pode melhorar!", response.getDescricao());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(filaFeedbackService, times(1)).enviarSQS(any(Feedback.class));
        verify(alertFeedbackService, never()).enviarSNS(any(Feedback.class));
    }

    @Test
    void criarAvaliacaoUrgenciaBaixa() {
        FeedbackRequest request = new FeedbackRequest(10, "Aula excelente!");

        var response =  service.executar(request);

        assertEquals(Urgencia.BAIXA, response.getUrgencia());
        assertEquals("Aula excelente!", response.getDescricao());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(filaFeedbackService, times(1)).enviarSQS(any(Feedback.class));
        verify(alertFeedbackService, never()).enviarSNS(any(Feedback.class));
    }

    @Test
    void erroAoSalvarAvaliacao(){
        FeedbackRequest request = new FeedbackRequest(10, "Aula excelente!");
        doThrow(PersistenceException.class).when(feedbackRepository).save(any(Feedback.class));

        assertThrows(PersistenceException.class, () -> service.executar(request));

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(filaFeedbackService, never()).enviarSQS(any(Feedback.class));
        verify(alertFeedbackService, never()).enviarSNS(any(Feedback.class));
    }

    @Test
    void erroAoMandarMensagemSQS(){
        FeedbackRequest request = new FeedbackRequest(10, "Aula excelente!");
        doThrow(ErroSQSException.class).when(filaFeedbackService).enviarSQS(any(Feedback.class));

        assertThrows(ErroSQSException.class, () -> service.executar(request));

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(filaFeedbackService, times(1)).enviarSQS(any(Feedback.class));
        verify(alertFeedbackService, never()).enviarSNS(any(Feedback.class));
    }

    @Test
    void erroAoMandarMensagemAlertaSNS(){
        FeedbackRequest request = new FeedbackRequest(3, "Aula pessima!");
        doThrow(ErroSNSException.class).when(alertFeedbackService).enviarSNS(any(Feedback.class));

        assertThrows(ErroSNSException.class, () -> service.executar(request));

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(filaFeedbackService, times(1)).enviarSQS(any(Feedback.class));
        verify(alertFeedbackService, times(1)).enviarSNS(any(Feedback.class));
    }
}
