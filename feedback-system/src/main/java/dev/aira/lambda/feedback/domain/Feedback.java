package dev.aira.lambda.feedback.domain;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@DynamoDbBean
public class Feedback {
    private String id;
    private Urgencia urgencia;
    private Integer nota;
    private String descricao;
    private String criadoEm;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public Urgencia getUrgencia() {
        return urgencia;
    }

    public Integer getNota() {
        return nota;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCriadoEm() {
        return criadoEm;
    }

    public Feedback() {
    }

    public Feedback(String id, Integer nota, String descricao) {
        this.id = id;
        this.nota = nota;
        this.urgencia = calcularUrgencia();
        this.descricao = descricao;
        criadoEm = Instant.now().toString();
    }

    private Urgencia calcularUrgencia() {
        if (nota <= 4) return Urgencia.ALTA;
        if (nota <= 6) return Urgencia.MEDIA;
        return Urgencia.BAIXA;
    }

    public boolean isUrgenciaAlta() {
        return urgencia == Urgencia.ALTA;
    }
}
