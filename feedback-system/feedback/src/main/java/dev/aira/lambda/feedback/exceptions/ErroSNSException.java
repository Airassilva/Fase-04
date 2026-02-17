package dev.aira.lambda.feedback.exceptions;

public class ErroSNSException extends RuntimeException {
    public ErroSNSException(Exception e) {
        super("Erro ao enviar mensagem para SNS", e);
    }
}
