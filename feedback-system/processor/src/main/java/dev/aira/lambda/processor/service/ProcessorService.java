package dev.aira.lambda.processor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aira.lambda.processor.domain.Relatorio;
import dev.aira.lambda.processor.dto.FeedbackMessage;
import dev.aira.lambda.processor.repository.RelatorioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@ApplicationScoped
public class ProcessorService {

    private static final Logger log = LoggerFactory.getLogger(ProcessorService.class);
    private final RelatorioRepository  relatorioRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public ProcessorService(RelatorioRepository relatorioRepository, ObjectMapper objectMapper) {
        this.relatorioRepository = relatorioRepository;
        this.objectMapper = objectMapper;
    }

    public void processarFeedback (String message) {
        try {
            FeedbackMessage  feedbackMessage = objectMapper.readValue(message,FeedbackMessage.class);
            log.info("[PROCESSAR FEEDBACK] {}", feedbackMessage);

            Relatorio relatorio = new Relatorio(
                    "FEEDBACK",
                    feedbackMessage.getFeedbackId(),
                    feedbackMessage.getUrgencia(),
                    feedbackMessage.getDescricao(),
                    feedbackMessage.getDataEnvio()
            );

            persistirDados(relatorio);
            atualizarAgregacaoPorDia(feedbackMessage.getDataEnvio());
            atualizarAgregacaoPorUrgencia(feedbackMessage.getUrgencia());

        }catch (JsonProcessingException e) {
            log.info("Erro ao processar feedback message", e);
        }
    }

    public void persistirDados(Relatorio relatorio) {
        relatorioRepository.save(relatorio);
    }

    private void atualizarAgregacaoPorDia(String dataEnvio) {
        LocalDate dia = Instant.parse(dataEnvio)
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        String chave = dia.toString();

        Relatorio agregacao = relatorioRepository.obterPoTipoEChave("AGR_DIA", chave);

        if (agregacao == null) {
            agregacao = new Relatorio();
            agregacao.setTipo("AGR_DIA");
            agregacao.setChave(chave);
            agregacao.setQuantidade(1);
        } else {
            agregacao.setQuantidade(agregacao.getQuantidade() + 1);
        }

        log.info("[ATUALIZAR DADOS DE AGREGACAO POR DIA] {}", agregacao);
        relatorioRepository.save(agregacao);
    }

    private void atualizarAgregacaoPorUrgencia(String urgencia) {
        Relatorio agregacao = relatorioRepository.obterPoTipoEChave("AGR_URGENCIA", urgencia);

        if (agregacao == null) {
            agregacao = new Relatorio();
            agregacao.setTipo("AGR_URGENCIA");
            agregacao.setChave(urgencia);
            agregacao.setQuantidade(1);
        } else {
            agregacao.setQuantidade(agregacao.getQuantidade() + 1);
        }

        log.info("[ATUALIZAR DADOS DE AGREGACAO POR URGENCIA] {}", agregacao);
        relatorioRepository.save(agregacao);
    }
}
