package dev.aira.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import dev.aira.lambda.processor.resource.ProcessorResourceHandler;
import dev.aira.lambda.processor.service.ProcessorService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@QuarkusTest
class ProcessorResourceHandlerTest {

    @Inject
    ProcessorResourceHandler handler;

    @InjectMock
    ProcessorService processorService;

    Context context;

    @BeforeEach
    void setUp() {
        context = Mockito.mock(Context.class);
        reset(processorService);
    }

    @Test
    void handleRequestComUmaMensagem() {
        SQSEvent sqsEvent = criarSQSEvent("mensagem-1", "Feedback 1");

        handler.handleRequest(sqsEvent, context);

        verify(processorService, times(1)).processarFeedback("Feedback 1");
    }

    @Test
    void handleRequestComVariasMensagens() {
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(java.util.Arrays.asList(
                criarSQSMessage("msg-1", "Feedback 1"),
                criarSQSMessage("msg-2", "Feedback 2"),
                criarSQSMessage("msg-3", "Feedback 3")
        ));

        handler.handleRequest(sqsEvent, context);

        verify(processorService, times(3)).processarFeedback(anyString());
        verify(processorService, times(1)).processarFeedback("Feedback 1");
        verify(processorService, times(1)).processarFeedback("Feedback 2");
        verify(processorService, times(1)).processarFeedback("Feedback 3");
    }

    @Test
    void handleRequestComListaVazia() {
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(java.util.Collections.emptyList());

        handler.handleRequest(sqsEvent, context);

        verify(processorService, never()).processarFeedback(anyString());
    }

    @Test
    void handleRequestComErroAoProcessar() {
        SQSEvent sqsEvent = criarSQSEvent("msg-erro", "Feedback com erro");
        doThrow(RuntimeException.class).when(processorService).processarFeedback("Feedback com erro");

        handler.handleRequest(sqsEvent, context);

        verify(processorService, times(1)).processarFeedback("Feedback com erro");
    }

    @Test
    void handleRequestRetornaNull() {
        SQSEvent sqsEvent = criarSQSEvent("msg-1", "Feedback 1");

        Void resultado = handler.handleRequest(sqsEvent, context);

        assertNull(resultado);
    }

    @Test
    void handleRequestProcessaTodasAsMensagensAindaComErro() {
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(java.util.Arrays.asList(
                criarSQSMessage("msg-1", "Feedback 1"),
                criarSQSMessage("msg-erro", "Feedback com erro"),
                criarSQSMessage("msg-3", "Feedback 3")
        ));
        doThrow(RuntimeException.class).when(processorService).processarFeedback("Feedback com erro");

        handler.handleRequest(sqsEvent, context);

        verify(processorService, times(3)).processarFeedback(anyString());
        verify(processorService, times(1)).processarFeedback("Feedback 1");
        verify(processorService, times(1)).processarFeedback("Feedback com erro");
        verify(processorService, times(1)).processarFeedback("Feedback 3");
    }


    private SQSEvent criarSQSEvent(String messageId, String body) {
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(java.util.Collections.singletonList(
                criarSQSMessage(messageId, body)
        ));
        return sqsEvent;
    }

    private SQSEvent.SQSMessage criarSQSMessage(String messageId, String body) {
        SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
        sqsMessage.setMessageId(messageId);
        sqsMessage.setBody(body);
        return sqsMessage;
    }
}
