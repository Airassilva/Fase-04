package dev.aira.lambda.processor.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import dev.aira.lambda.processor.service.ProcessorService;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("processor-feedback")
public class ProcessorResourceHandler implements RequestHandler <SQSEvent, Void>{

    private final Logger log = LoggerFactory.getLogger(ProcessorResourceHandler.class);
    private final ProcessorService  processorService;

    public ProcessorResourceHandler(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
       for(SQSEvent.SQSMessage sqsMessage : sqsEvent.getRecords()) {
           try {
               String message = sqsMessage.getBody();
               log.info("Processando evento com mensagem: {}", message);
               processorService.processarFeedback(message);
           } catch (Exception e) {
               log.error("Erro ao processar mensagem {}", sqsMessage.getBody(), e);
           }
       }
       return null;
    }
}
