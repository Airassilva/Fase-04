package ut;

import dev.aira.lambda.report.domain.Relatorio;
import dev.aira.lambda.report.dto.DadosRelatorioSemanal;
import dev.aira.lambda.report.repository.RelatorioRepository;
import dev.aira.lambda.report.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    RelatorioRepository repository;

    private RelatorioService service;

    @BeforeEach
    void setUp() {
        service = new RelatorioService(repository);
    }

    private Relatorio criarRelatorio(String chave, Integer quantidade) {
        Relatorio relatorio = new Relatorio();
        relatorio.setChave(chave);
        relatorio.setQuantidade(quantidade);
        return relatorio;
    }

    private Relatorio criarRelatorioComQuantidadeNula(String chave) {
        Relatorio relatorio = new Relatorio();
        relatorio.setChave(chave);
        relatorio.setQuantidade(null);
        return relatorio;
    }

    @Test
    void gerarDadosSemanaAtualComSucesso() {
        List<Relatorio> diasBanco = Arrays.asList(
                criarRelatorio("2024-02-12", 5),
                criarRelatorio("2024-02-13", 8)
        );
        List<Relatorio> urgencias = Arrays.asList(
                criarRelatorio("ALTA", 10),
                criarRelatorio("MEDIA", 15)
        );

        when(repository.obterAgregacaoDia(anyString(), anyString())).thenReturn(diasBanco);
        when(repository.obterAgregacaoUrgencia()).thenReturn(urgencias);

        DadosRelatorioSemanal resultado = service.gerarDadosSemanaAtual();

        assertNotNull(resultado);
        assertNotNull(resultado.getDias());
        assertNotNull(resultado.getUrgencias());
        assertEquals(7, resultado.getDias().size());
        assertEquals(2, resultado.getUrgencias().size());
    }

    @Test
    void gerarDadosSemanaAtualRetornaUrgencias() {
        List<Relatorio> diasBanco = List.of();
        List<Relatorio> urgencias = Arrays.asList(
                criarRelatorio("ALTA", 10),
                criarRelatorio("MEDIA", 15),
                criarRelatorio("BAIXA", 5)
        );

        when(repository.obterAgregacaoDia(anyString(), anyString())).thenReturn(diasBanco);
        when(repository.obterAgregacaoUrgencia()).thenReturn(urgencias);

        DadosRelatorioSemanal resultado = service.gerarDadosSemanaAtual();

        assertNotNull(resultado.getUrgencias());
        assertEquals(3, resultado.getUrgencias().size());
    }

    @Test
    void gerarDadosSemanaAtualComQuantidadeNula() {
        List<Relatorio> diasBanco = List.of(
                criarRelatorioComQuantidadeNula("2024-02-17")
        );
        List<Relatorio> urgencias = List.of();

        when(repository.obterAgregacaoDia(anyString(), anyString())).thenReturn(diasBanco);
        when(repository.obterAgregacaoUrgencia()).thenReturn(urgencias);

        DadosRelatorioSemanal resultado = service.gerarDadosSemanaAtual();

        assertNotNull(resultado.getDias());
        assertEquals(7, resultado.getDias().size());
        assertTrue(resultado.getDias().stream().allMatch(d -> d.getQuantidade() >= 0));
    }

    @Test
    void gerarDadosSemanaAtualComDiasVazios() {
        List<Relatorio> diasBanco = List.of();
        List<Relatorio> urgencias = List.of();

        when(repository.obterAgregacaoDia(anyString(), anyString())).thenReturn(diasBanco);
        when(repository.obterAgregacaoUrgencia()).thenReturn(urgencias);

        DadosRelatorioSemanal resultado = service.gerarDadosSemanaAtual();

        assertNotNull(resultado);
        assertEquals(7, resultado.getDias().size());
        assertTrue(resultado.getDias().stream().allMatch(d -> d.getQuantidade() == 0));
    }

    @Test
    void gerarDadosSemanaAtualComMultiplosDias() {
        List<Relatorio> diasBanco = Arrays.asList(
                criarRelatorio("2024-02-12", 5),
                criarRelatorio("2024-02-13", 8),
                criarRelatorio("2024-02-14", 10),
                criarRelatorio("2024-02-15", 7),
                criarRelatorio("2024-02-16", 6),
                criarRelatorio("2024-02-17", 9),
                criarRelatorio("2024-02-18", 4)
        );
        List<Relatorio> urgencias = List.of();

        when(repository.obterAgregacaoDia(anyString(), anyString())).thenReturn(diasBanco);
        when(repository.obterAgregacaoUrgencia()).thenReturn(urgencias);

        DadosRelatorioSemanal resultado = service.gerarDadosSemanaAtual();

        assertEquals(7, resultado.getDias().size());
    }

    @Test
    void gerarDadosSemanaAtualComRepositoryVazio() {
        when(repository.obterAgregacaoDia(anyString(), anyString())).thenReturn(List.of());
        when(repository.obterAgregacaoUrgencia()).thenReturn(List.of());

        DadosRelatorioSemanal resultado = service.gerarDadosSemanaAtual();

        assertNotNull(resultado);
        assertNotNull(resultado.getDias());
        assertNotNull(resultado.getUrgencias());
        assertEquals(7, resultado.getDias().size());
        assertTrue(resultado.getUrgencias().isEmpty());
    }

    @Test
    void gerarDadosSemanaAtualComErroAoObterDias() {
        when(repository.obterAgregacaoDia(anyString(), anyString())).thenThrow(new RuntimeException("Erro ao obter dados"));

        assertThrows(RuntimeException.class, () -> service.gerarDadosSemanaAtual());

        verify(repository, times(1)).obterAgregacaoDia(anyString(), anyString());
    }

    @Test
    void gerarDadosSemanaAtualComErroAoObterUrgencias() {
        List<Relatorio> diasBanco = List.of();

        when(repository.obterAgregacaoDia(anyString(), anyString())).thenReturn(diasBanco);
        when(repository.obterAgregacaoUrgencia()).thenThrow(new RuntimeException("Erro ao obter urgências"));

        assertThrows(RuntimeException.class, () -> service.gerarDadosSemanaAtual());

        verify(repository, times(1)).obterAgregacaoUrgencia();
    }
}
