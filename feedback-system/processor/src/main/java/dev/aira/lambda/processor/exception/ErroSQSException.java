package dev.aira.lambda.processor.exception;

public class ErroSQSException extends RuntimeException {
    public ErroSQSException(Exception e) {
        super("Erro ao processar mensagem da fila", e);
    }
}
