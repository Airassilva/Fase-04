package dev.aira.lambda.report.exception;

public class ErroConsultationDbException extends RuntimeException {
    public ErroConsultationDbException(Exception e) {
        super("Erro ao consultar chave e chave no dynamoDb", e);
    }
}
