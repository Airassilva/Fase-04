package dev.aira.lambda.processor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedbackMessage {
    @JsonProperty("feedbackId")
    private String feedbackId;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("urgencia")
    private String urgencia;

    @JsonProperty("dataEnvio")
    private String dataEnvio;

    public FeedbackMessage() {
    }

    public FeedbackMessage(String feedbackId, String descricao, String urgencia, String dataEnvio) {
        this.feedbackId = feedbackId;
        this.descricao = descricao;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
    }

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
}
