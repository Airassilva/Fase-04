package ut;

import dev.aira.lambda.report.domain.Relatorio;
import dev.aira.lambda.report.dto.DadosRelatorioSemanal;
import dev.aira.lambda.report.dto.DiaRelatorio;
import dev.aira.lambda.report.exception.ErroGerarPdfException;
import dev.aira.lambda.report.service.GerarPdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class GerarPdfServiceTest {

    private GerarPdfService service;

    @BeforeEach
    void setUp() {
        service = new GerarPdfService();
    }

    private DadosRelatorioSemanal criarDadosRelatorio() {
        List<DiaRelatorio> dias = List.of(
                criarDiaRelatorio("2024-02-17", 10)
        );

        List<Relatorio> urgencias = List.of(
                criarRelatorioUrgencia("MEDIA", 10)
        );

        return new DadosRelatorioSemanal(dias, urgencias);
    }

    private DiaRelatorio criarDiaRelatorio(String data, Integer quantidade) {
        return new DiaRelatorio(data, quantidade);
    }

    private Relatorio criarRelatorioUrgencia(String urgencia, Integer quantidade) {
        Relatorio relatorio = new Relatorio();
        relatorio.setChave(urgencia);
        relatorio.setQuantidade(quantidade);
        return relatorio;
    }

    @Test
    void gerarPdfComSucesso() {
        DadosRelatorioSemanal dados = criarDadosRelatorio();

        byte[] resultado = service.gerarPdf(dados);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    @Test
    void gerarPdfComDadosVazios() {
        DadosRelatorioSemanal dados = new DadosRelatorioSemanal(
                List.of(),
                List.of()
        );

        byte[] resultado = service.gerarPdf(dados);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    @Test
    void gerarPdfComMultiplosDias() {
        List<DiaRelatorio> dias = Arrays.asList(
                criarDiaRelatorio("2024-02-12", 5),
                criarDiaRelatorio("2024-02-13", 8),
                criarDiaRelatorio("2024-02-14", 10)
        );

        List<Relatorio> urgencias = Arrays.asList(
                criarRelatorioUrgencia("ALTA", 15),
                criarRelatorioUrgencia("MEDIA", 20)
        );

        DadosRelatorioSemanal dados = new DadosRelatorioSemanal(dias, urgencias);

        byte[] resultado = service.gerarPdf(dados);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    @Test
    void gerarPdfComMultiplasUrgencias() {
        List<DiaRelatorio> dias = Arrays.asList(
                criarDiaRelatorio("2024-02-12", 5)
        );

        List<Relatorio> urgencias = Arrays.asList(
                criarRelatorioUrgencia("ALTA", 10),
                criarRelatorioUrgencia("MEDIA", 15),
                criarRelatorioUrgencia("BAIXA", 5)
        );

        DadosRelatorioSemanal dados = new DadosRelatorioSemanal(dias, urgencias);

        byte[] resultado = service.gerarPdf(dados);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    @Test
    void gerarPdfComDadosNulos() {
        assertThrows(ErroGerarPdfException.class,
                () -> service.gerarPdf(null));
    }

    @Test
    void gerarPdfComDiasNulos() {
        DadosRelatorioSemanal dados = new DadosRelatorioSemanal(null, List.of());

        assertThrows(ErroGerarPdfException.class,
                () -> service.gerarPdf(dados));
    }

    @Test
    void gerarPdfComUrgenciasNulas() {
        DadosRelatorioSemanal dados = new DadosRelatorioSemanal(List.of(), null);

        assertThrows(ErroGerarPdfException.class,
                () -> service.gerarPdf(dados));
    }
}
