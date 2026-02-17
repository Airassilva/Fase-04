package dev.aira.lambda.feedback.exceptions;

public class PersistenceException extends RuntimeException {
    public PersistenceException(Exception e) {
        super("Erro ao persistir feedback no dynamoDb", e);
    }
}
