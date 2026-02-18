package dev.aira.lambda.report.exception;

public class ErroGerarPdfException extends RuntimeException {
    public ErroGerarPdfException(Exception e) {
        super("Erro ao gerar PDF", e);
    }
}
