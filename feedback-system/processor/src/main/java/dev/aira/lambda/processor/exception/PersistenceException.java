package dev.aira.lambda.processor.exception;

public class PersistenceException extends RuntimeException {
    public PersistenceException(Exception e) {
        super("Erro ao persistir relatorio no dynamodb", e);
    }
}
