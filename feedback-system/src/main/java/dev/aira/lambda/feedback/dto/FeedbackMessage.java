package dev.aira.lambda.feedback.dto;

import dev.aira.lambda.feedback.domain.Feedback;

public class FeedbackMessage {
    private String feedbackId;
    private String descricao;
    private String urgencia;
    private String dataEnvio;

    public String getFeedbackId() {
        return feedbackId;
    }

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
        this.feedbackId = feedback.getId();
        this.descricao = feedback.getDescricao();
        this.urgencia = feedback.getUrgencia().name();
        this.dataEnvio = feedback.getCriadoEm();
    }
}
