package ut;

import com.amazonaws.services.lambda.runtime.Context;
import dev.aira.lambda.report.domain.Relatorio;
import dev.aira.lambda.report.dto.DadosRelatorioSemanal;
import dev.aira.lambda.report.dto.DiaRelatorio;
import dev.aira.lambda.report.exception.*;
import dev.aira.lambda.report.infrastructure.ReportSNSProducer;
import dev.aira.lambda.report.resource.ReportHandlerResource;
import dev.aira.lambda.report.service.GerarPdfService;
import dev.aira.lambda.report.service.RelatorioService;
import dev.aira.lambda.report.infrastructure.S3Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportHandlerResourceTest {

    @Mock
    GerarPdfService gerarPdfService;

    @Mock
    S3Producer s3Producer;

    @Mock
    ReportSNSProducer reportSNSProducer;

    @Mock
    RelatorioService relatorioService;

    @Mock
    Context context;

    private ReportHandlerResource handler;

    @BeforeEach
    void setUp() {
        handler = new ReportHandlerResource(gerarPdfService, s3Producer, reportSNSProducer, relatorioService);
    }

    private DadosRelatorioSemanal criarDadosRelatorio() {
        List<DiaRelatorio> dias = Mockito.mock(List.class);
        List<Relatorio> urgencias = Mockito.mock(List.class);
        return new DadosRelatorioSemanal(dias, urgencias);
    }

    @Test
    void gerarPdfRelatorio(){
        DadosRelatorioSemanal dados = criarDadosRelatorio();
        byte[] pdfBytes = "PDF_CONTENT".getBytes();
        String nomeArquivo = "Relatorio-semanal-2024-02-17.pdf";
        String urlPresignada = "https://s3.amazonaws.com/bucket/Relatorio-semanal-2024-02-17.pdf";

        when(relatorioService.gerarDadosSemanaAtual()).thenReturn(dados);
        when(gerarPdfService.gerarPdf(dados)).thenReturn(pdfBytes);
        when(s3Producer.uploadRelatorio(any(byte[].class), anyString())).thenReturn(nomeArquivo);
        when(s3Producer.gerarPresignedUrl(nomeArquivo)).thenReturn(urlPresignada);

        String resultado = handler.handleRequest(new HashMap<>(), context);

        assertNotNull(resultado);
        assertTrue(resultado.contains("Relatorio enviado com sucesso!"));
        assertTrue(resultado.contains(urlPresignada));

        verify(relatorioService, times(1)).gerarDadosSemanaAtual();
        verify(gerarPdfService, times(1)).gerarPdf(dados);
        verify(s3Producer, times(1)).uploadRelatorio(any(byte[].class), anyString());
        verify(s3Producer, times(1)).gerarPresignedUrl(nomeArquivo);
        verify(reportSNSProducer, times(1)).enviarAlerta(urlPresignada);
    }

    @Test
    void erroAoFazerUploadS3() {
        DadosRelatorioSemanal dados = criarDadosRelatorio();
        byte[] pdfBytes = "PDF_CONTENT".getBytes();

        when(relatorioService.gerarDadosSemanaAtual()).thenReturn(dados);
        when(gerarPdfService.gerarPdf(dados)).thenReturn(pdfBytes);
        when(s3Producer.uploadRelatorio(any(byte[].class), anyString())).thenThrow(RuntimeException.class);

        assertThrows(ErroProcessarRelatorioException.class,
                () -> handler.handleRequest(new HashMap<>(), context));

        verify(s3Producer, times(1)).uploadRelatorio(any(byte[].class), anyString());
        verify(reportSNSProducer, never()).enviarAlerta(anyString());
    }

    @Test
    void erroAoGerarUrl() {
        DadosRelatorioSemanal dados = criarDadosRelatorio();
        byte[] pdfBytes = "PDF_CONTENT".getBytes();
        String nomeArquivo = "Relatorio-semanal.pdf";

        when(relatorioService.gerarDadosSemanaAtual()).thenReturn(dados);
        when(gerarPdfService.gerarPdf(dados)).thenReturn(pdfBytes);
        when(s3Producer.uploadRelatorio(any(byte[].class), anyString())).thenReturn(nomeArquivo);
        when(s3Producer.gerarPresignedUrl(nomeArquivo)).thenThrow(RuntimeException.class);

        assertThrows(ErroProcessarRelatorioException.class,
                () -> handler.handleRequest(new HashMap<>(), context));

        verify(s3Producer, times(1)).gerarPresignedUrl(nomeArquivo);
        verify(reportSNSProducer, never()).enviarAlerta(anyString());
    }

    @Test
    void retornaStringComUrl() {
        DadosRelatorioSemanal dados = criarDadosRelatorio();
        byte[] pdfBytes = "PDF_CONTENT".getBytes();
        String nomeArquivo = "Relatorio-semanal.pdf";
        String urlPresignada = "https://s3.amazonaws.com/bucket/relatorio.pdf";

        when(relatorioService.gerarDadosSemanaAtual()).thenReturn(dados);
        when(gerarPdfService.gerarPdf(dados)).thenReturn(pdfBytes);
        when(s3Producer.uploadRelatorio(any(byte[].class), anyString())).thenReturn(nomeArquivo);
        when(s3Producer.gerarPresignedUrl(nomeArquivo)).thenReturn(urlPresignada);

        String resultado = handler.handleRequest(new HashMap<>(), context);

        assertNotNull(resultado);
        assertTrue(resultado.contains(urlPresignada));
    }
}
