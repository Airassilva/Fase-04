package dev.aira.lambda.report.exception;

public class ErroSNSException extends RuntimeException {
    public ErroSNSException(Exception e) {
        super("Erro ao enviar mensagem para SNS", e);
    }
}
