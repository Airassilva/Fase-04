package dev.aira.lambda.report.exception;

public class ErroProcessarRelatorioException extends RuntimeException {
    public ErroProcessarRelatorioException(Exception e) {
        super("Ocorreu um erro ao processar o relatório",e);
    }
}
