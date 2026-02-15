package dev.aira.lambda.feedback.dto;

import dev.aira.lambda.feedback.domain.Feedback;

public class FeedbackMessage {
    private String descricao;
    private String urgencia;
    private String dataEnvio;

    public String getDescricao() {
        return descricao;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public FeedbackMessage(Feedback feedback) {
        this.descricao = feedback.getDescricao();
        this.urgencia = feedback.getUrgencia().name();
        this.dataEnvio = feedback.getCriadoEm();
    }
}
