package dev.aira.lambda.report.dto;

import dev.aira.lambda.report.domain.Relatorio;

import java.util.List;

public class DadosRelatorioSemanal {

    private List<DiaRelatorio> dias;
    private List<Relatorio> urgencias;

    public DadosRelatorioSemanal(List<DiaRelatorio> dias, List<Relatorio> urgencias) {
        this.dias = dias;
        this.urgencias = urgencias;
    }

    public List<DiaRelatorio> getDias() {
        return dias;
    }
    public List<Relatorio> getUrgencias() {
        return urgencias;
    }
}
