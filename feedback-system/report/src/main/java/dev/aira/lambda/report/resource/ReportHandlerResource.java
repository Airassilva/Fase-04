package dev.aira.lambda.report.resource;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dev.aira.lambda.report.dto.DadosRelatorioSemanal;
import dev.aira.lambda.report.exception.ErroProcessarRelatorioException;
import dev.aira.lambda.report.infrastructure.ReportSNSProducer;
import dev.aira.lambda.report.service.GerarPdfService;
import dev.aira.lambda.report.service.RelatorioService;
import dev.aira.lambda.report.infrastructure.S3Producer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@ApplicationScoped
@Named("report-feedback")
public class ReportHandlerResource implements RequestHandler<Map<String, Object>, String> {

    private final GerarPdfService gerarPdfService;
    private final S3Producer s3Producer;
    private final ReportSNSProducer  reportSNSProducer;
    private final RelatorioService  relatorioService;

    @Inject
    public ReportHandlerResource(GerarPdfService gerarPdfService, S3Producer s3Producer, ReportSNSProducer reportSNSProducer, RelatorioService relatorioService) {
        this.gerarPdfService = gerarPdfService;
        this.s3Producer = s3Producer;
        this.reportSNSProducer = reportSNSProducer;
        this.relatorioService = relatorioService;
    }

    @Override
    public String handleRequest(Map<String, Object> stringObjectMap, Context context) {
        try{
            DadosRelatorioSemanal dados = relatorioService.gerarDadosSemanaAtual();
            byte[] pdfBytes = gerarPdfService.gerarPdf(dados);
            String nomeArquivo = s3Producer.uploadRelatorio(pdfBytes,
                    "Relatorio-semanal" + LocalDateTime.now(ZoneOffset.UTC) + ".pdf");
            String url = s3Producer.gerarPresignedUrl(nomeArquivo);
            reportSNSProducer.enviarAlerta(url);
            return "Relatorio enviado com sucesso!" + url;

        }catch (Exception e){
            throw new ErroProcessarRelatorioException(e);
        }
    }
}
