package dev.aira.lambda.processor.repository;

import dev.aira.lambda.processor.domain.Relatorio;

public interface RelatorioRepository {
    void save(Relatorio relatorio);
    Relatorio obterPoTipoEChave(String tipo, String chave);
}
