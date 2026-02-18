package dev.aira.lambda.report.repository;

import dev.aira.lambda.report.domain.Relatorio;
import dev.aira.lambda.report.exception.ErroConsultationDbException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@ApplicationScoped
public class RelatorioDynamoRepository implements RelatorioRepository {

    private static final Logger log = LoggerFactory.getLogger(RelatorioDynamoRepository.class);
    private static final TableSchema<Relatorio> SCHEMA = TableSchema.fromBean(Relatorio.class);
    private final DynamoDbTable<Relatorio> table;

    public RelatorioDynamoRepository( DynamoDbEnhancedClient enhancedClient,
                                      @ConfigProperty(name = "relatorio.table.name")
                                      String tableName) {
        this.table = enhancedClient.table(tableName, SCHEMA);
    }

    public Relatorio obterPorTipoEChave(String tipo, String chave) {
        try {
            log.info("obter po tipo chave {}", chave);
            return table.getItem(Key.builder()
                    .partitionValue(tipo)
                    .sortValue(chave)
                    .build());
        } catch (Exception e) {
            throw new ErroConsultationDbException(e);
        }
    }

    @Override
    public Relatorio obterFeedback(String feedbackId) {
        return obterPorTipoEChave("FEEDBACK", feedbackId);

    }

    @Override
    public List<Relatorio> obterAgregacaoDia(String dataInicio, String dataFim) {
        QueryConditional conditional = QueryConditional.sortBetween(
                Key.builder()
                        .partitionValue("AGR_DIA")
                        .sortValue(dataInicio)
                        .build(),
                Key.builder()
                        .partitionValue("AGR_DIA")
                        .sortValue(dataFim)
                        .build()
        );

        return table.query(r -> r.queryConditional(conditional))
                .items()
                .stream()
                .toList();
    }

    @Override
    public List<Relatorio> obterAgregacaoUrgencia() {
        QueryConditional conditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue("AGR_URGENCIA")
                        .build());

        return table.query(r -> r.queryConditional(conditional))
                .items()
                .stream()
                .toList();
    }
}
