package dev.aira.lambda.ut;

import dev.aira.lambda.feedback.domain.Urgencia;
import dev.aira.lambda.feedback.dto.FeedbackRequest;
import dev.aira.lambda.feedback.dto.FeedbackResponse;
import dev.aira.lambda.feedback.resource.FeedbackResource;
import dev.aira.lambda.feedback.service.FeedbackService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


@QuarkusTest
class FeedbackResourceTest {

    @Inject
    FeedbackResource feedbackResource;

    @InjectMock
    FeedbackService feedbackService;

    @BeforeEach
    void setup() {
        reset(feedbackService);
    }

    @ParameterizedTest
    @CsvSource({
            "3, Aula péssima!, ALTA",
            "6,A aula pode melhorar!, MEDIA",
            "10, Aula Excelente!, BAIXA"
    })
    void criarAvaliacaoComUrgenciaValida(int nota, String descricao, Urgencia urgenciaEsperada) {
        var feedbackRequest = new FeedbackRequest(nota, descricao);
        var feedbackResponse = new FeedbackResponse(
                UUID.randomUUID().toString(),
                urgenciaEsperada,
                Instant.now().toString(),
                descricao
        );
        when(feedbackService.executar(feedbackRequest)).thenReturn(feedbackResponse);

        var response = feedbackResource.enviarFeedback(feedbackRequest);
        var entity = (FeedbackResponse) response.getEntity();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(urgenciaEsperada, entity.getUrgencia());
        assertEquals(descricao, entity.getDescricao());
    }


    @Test
    void criarAvaliacaoComNotaInvalida(){
        var feedbackRequest = new FeedbackRequest(-10, "Nota Invalida!");
        assertThrows(ConstraintViolationException.class,
                () -> feedbackResource.enviarFeedback(feedbackRequest));
    }

    @Test
    void criarAvaliacaoComDescricaoInvalida(){
        var feedbackRequest = new FeedbackRequest(2, " ");
        assertThrows(ConstraintViolationException.class,
                () -> feedbackResource.enviarFeedback(feedbackRequest));
    }
}
