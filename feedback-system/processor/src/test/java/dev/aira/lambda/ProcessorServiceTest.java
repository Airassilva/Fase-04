package dev.aira.lambda;

import dev.aira.lambda.processor.domain.Relatorio;
import dev.aira.lambda.processor.exception.PersistenceException;
import dev.aira.lambda.processor.repository.RelatorioRepository;
import dev.aira.lambda.processor.service.ProcessorService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class ProcessorServiceTest {

    @Inject
    ProcessorService service;

    @InjectMock
    RelatorioRepository relatorioRepository;

    @BeforeEach
     void setup() {
        reset(relatorioRepository);
    }

    @Test
    void processarFeedbackComSucesso(){
        String messageBody = "{\"feedbackId\":\"123\",\"descricao\":\"Bom serviço\",\"urgencia\":\"MEDIA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        when(relatorioRepository.obterPorTipoEChave("AGR_DIA", "2024-02-17")).thenReturn(null);
        when(relatorioRepository.obterPorTipoEChave("AGR_URGENCIA", "MEDIA")).thenReturn(null);

        service.processarFeedback(messageBody);

        verify(relatorioRepository, times(3)).save(any(Relatorio.class));
    }

    @Test
    void atualizarAgregacaoPorDiaComNovaEntrada(){
        String messageBody = "{\"feedbackId\":\"456\",\"descricao\":\"Serviço rápido\",\"urgencia\":\"ALTA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        when(relatorioRepository.obterPorTipoEChave("AGR_DIA", "2024-02-17")).thenReturn(null);
        when(relatorioRepository.obterPorTipoEChave("AGR_URGENCIA", "ALTA")).thenReturn(null);

        service.processarFeedback(messageBody);

        verify(relatorioRepository, times(1)).obterPorTipoEChave("AGR_DIA", "2024-02-17");
        verify(relatorioRepository, times(3)).save(any(Relatorio.class));
    }

    @Test
    void atualizarAgregacaoPorDiaComEntradaExistente(){
        String messageBody = "{\"feedbackId\":\"789\",\"descricao\":\"Atendimento bom\",\"urgencia\":\"BAIXA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        Relatorio agregacaoDiaExistente = new Relatorio();
        agregacaoDiaExistente.setTipo("AGR_DIA");
        agregacaoDiaExistente.setChave("2024-02-17");
        agregacaoDiaExistente.setQuantidade(5);

        when(relatorioRepository.obterPorTipoEChave("AGR_DIA", "2024-02-17")).thenReturn(agregacaoDiaExistente);
        when(relatorioRepository.obterPorTipoEChave("AGR_URGENCIA", "BAIXA")).thenReturn(null);

        service.processarFeedback(messageBody);

        verify(relatorioRepository, times(1)).obterPorTipoEChave("AGR_DIA", "2024-02-17");
        verify(relatorioRepository, times(3)).save(any(Relatorio.class));
    }

    @Test
    void atualizarAgregacaoPorUrgenciaComNovaEntrada() {
        String messageBody = "{\"feedbackId\":\"111\",\"descricao\":\"Crítico\",\"urgencia\":\"ALTA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        when(relatorioRepository.obterPorTipoEChave("AGR_DIA", "2024-02-17")).thenReturn(null);
        when(relatorioRepository.obterPorTipoEChave("AGR_URGENCIA", "ALTA")).thenReturn(null);

        service.processarFeedback(messageBody);

        verify(relatorioRepository, times(1)).obterPorTipoEChave("AGR_URGENCIA", "ALTA");
        verify(relatorioRepository, times(3)).save(any(Relatorio.class));
    }

    @Test
    void atualizarAgregacaoPorUrgenciaComEntradaExistente(){
        String messageBody = "{\"feedbackId\":\"222\",\"descricao\":\"Médio\",\"urgencia\":\"MEDIA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        Relatorio agregacaoUrgenciaExistente = new Relatorio();
        agregacaoUrgenciaExistente.setTipo("AGR_URGENCIA");
        agregacaoUrgenciaExistente.setChave("MEDIA");
        agregacaoUrgenciaExistente.setQuantidade(10);

        when(relatorioRepository.obterPorTipoEChave("AGR_DIA", "2024-02-17")).thenReturn(null);
        when(relatorioRepository.obterPorTipoEChave("AGR_URGENCIA", "MEDIA")).thenReturn(agregacaoUrgenciaExistente);

        service.processarFeedback(messageBody);

        verify(relatorioRepository, times(1)).obterPorTipoEChave("AGR_URGENCIA", "MEDIA");
        verify(relatorioRepository, times(3)).save(any(Relatorio.class));
    }

    @Test
    void persistirDadosComSucesso() {
        Relatorio relatorio = new Relatorio("FEEDBACK", "123", "ALTA", "Crítico", "2024-02-17");

        service.persistirDados(relatorio);

        verify(relatorioRepository, times(1)).save(relatorio);
    }

    @Test
    void erroAoDeserializarMensagem(){
        String messageBody = "{invalid json}";

        service.processarFeedback(messageBody);

        verify(relatorioRepository, never()).save(any(Relatorio.class));
    }

    @Test
    void erroAoSalvarFeedback(){
        String messageBody = "{\"feedbackId\":\"333\",\"descricao\":\"Teste\",\"urgencia\":\"BAIXA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        doThrow(PersistenceException.class).when(relatorioRepository).save(any(Relatorio.class));

        assertThrows(PersistenceException.class, () -> service.processarFeedback(messageBody));

        verify(relatorioRepository, times(1)).save(any(Relatorio.class));
        verify(relatorioRepository, never()).obterPorTipoEChave(anyString(), anyString());
    }

    @Test
    void erroAoAtualizarAgregacaoPorDia(){
        String messageBody = "{\"feedbackId\":\"444\",\"descricao\":\"Teste\",\"urgencia\":\"MEDIA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        when(relatorioRepository.obterPorTipoEChave("AGR_DIA", "2024-02-17")).thenReturn(null);
        doThrow(PersistenceException.class).when(relatorioRepository).save(any(Relatorio.class));

        assertThrows(PersistenceException.class, () -> service.processarFeedback(messageBody));

        verify(relatorioRepository, times(1)).save(any(Relatorio.class));
    }

    @Test
    void erroAoAtualizarAgregacaoPorUrgencia(){
        String messageBody = "{\"feedbackId\":\"555\",\"descricao\":\"Teste\",\"urgencia\":\"ALTA\",\"dataEnvio\":\"2024-02-17T10:00:00Z\"}";

        when(relatorioRepository.obterPorTipoEChave("AGR_DIA", "2024-02-17")).thenReturn(null);
        when(relatorioRepository.obterPorTipoEChave("AGR_URGENCIA", "ALTA")).thenReturn(null);
        doThrow(PersistenceException.class).when(relatorioRepository).save(any(Relatorio.class));

        assertThrows(PersistenceException.class, () -> service.processarFeedback(messageBody));

        verify(relatorioRepository, times(1)).save(any(Relatorio.class));
    }
}
