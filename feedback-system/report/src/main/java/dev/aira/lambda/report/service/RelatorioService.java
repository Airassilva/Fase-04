package dev.aira.lambda.report.service;

import dev.aira.lambda.report.domain.Relatorio;
import dev.aira.lambda.report.dto.DadosRelatorioSemanal;
import dev.aira.lambda.report.dto.DiaRelatorio;
import dev.aira.lambda.report.repository.RelatorioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RelatorioService {

    private final RelatorioRepository repository;

    @Inject
    public RelatorioService(RelatorioRepository repository) {
        this.repository = repository;
    }

    public DadosRelatorioSemanal gerarDadosSemanaAtual() {
        LocalDate hoje = LocalDate.now(ZoneOffset.UTC);
        LocalDate inicioSemana = hoje.minusDays(6);

        String inicio = inicioSemana.toString();
        String fim = hoje.toString();

        List<Relatorio> diasBanco =
                repository.obterAgregacaoDia(inicio, fim);

        Map<String, Integer> mapaDias = diasBanco.stream()
                .collect(Collectors.toMap(
                        Relatorio::getChave,
                        r -> r.getQuantidade() == null ? 0 : r.getQuantidade()
                ));

        List<DiaRelatorio> dias = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicioSemana.plusDays(i);
            String chave = dia.toString();

            dias.add(new DiaRelatorio(
                    chave,
                    mapaDias.getOrDefault(chave, 0)
            ));
        }

        List<Relatorio> urgencias =
                repository.obterAgregacaoUrgencia();

        return new DadosRelatorioSemanal(dias, urgencias);
    }
}
