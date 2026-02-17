package dev.aira.lambda.feedback.dto;

import dev.aira.lambda.feedback.domain.Urgencia;

public class FeedbackResponse {
    private String id;
    private Urgencia urgencia;
    private String dataEnvio;
    private String descricao;

    public String getId() {
        return id;
    }

    public Urgencia getUrgencia() {
        return urgencia;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public String getDescricao() {
        return descricao;
    }

    public FeedbackResponse(String id, Urgencia urgencia, String dataEnvio, String descricao) {
        this.id = id;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
        this.descricao = descricao;
    }
}
