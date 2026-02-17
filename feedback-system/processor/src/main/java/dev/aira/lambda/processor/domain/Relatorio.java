package dev.aira.lambda.processor.domain;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Relatorio {
    private String tipo; //pk
    private String chave; //sk
    private String urgencia;
    private String descricao;
    private String dataEnvio;
    private Integer quantidade;


    @DynamoDbPartitionKey
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @DynamoDbSortKey
    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(String urgencia) {
        this.urgencia = urgencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(String dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Relatorio() {
    }

    public Relatorio(String tipo, String chave, String urgencia, String descricao, String dataEnvio) {
        this.tipo = tipo;
        this.chave = chave;
        this.urgencia = urgencia;
        this.descricao = descricao;
        this.dataEnvio = dataEnvio;
    }
}
