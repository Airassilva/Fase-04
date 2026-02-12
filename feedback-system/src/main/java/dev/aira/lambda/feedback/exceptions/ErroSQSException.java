package dev.aira.lambda.feedback.exceptions;

public class ErroSQSException extends RuntimeException {
    public ErroSQSException(Exception e) {
        super("Erro ao enviar mensagem para SQS", e);
    }
}
