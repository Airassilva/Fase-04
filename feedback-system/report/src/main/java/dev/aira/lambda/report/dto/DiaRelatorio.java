package dev.aira.lambda.report.dto;

public class DiaRelatorio {

    private String data;
    private Integer quantidade;

    public DiaRelatorio(String data, Integer quantidade) {
        this.data = data;
        this.quantidade = quantidade;
    }

    public String getData() { return data; }
    public Integer getQuantidade() { return quantidade; }
}

