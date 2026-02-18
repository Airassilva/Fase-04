package dev.aira.lambda.report.repository;


import dev.aira.lambda.report.domain.Relatorio;

import java.util.List;

public interface RelatorioRepository {
    Relatorio obterPorTipoEChave(String tipo, String chave);
    Relatorio obterFeedback(String feedbackId);
    List<Relatorio> obterAgregacaoDia(String dataInicio, String dataFim);
    List<Relatorio> obterAgregacaoUrgencia();
}
