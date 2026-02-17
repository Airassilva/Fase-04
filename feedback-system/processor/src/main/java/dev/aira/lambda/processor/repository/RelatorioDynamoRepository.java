package dev.aira.lambda.processor.repository;

import dev.aira.lambda.processor.domain.Relatorio;
import dev.aira.lambda.processor.exception.ErroConsultationDbException;
import dev.aira.lambda.processor.exception.PersistenceException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

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

    @Override
    public void save(Relatorio relatorio) {
        try {
            table.putItem(relatorio);
            log.info("save relatorio {}", relatorio);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Relatorio obterPoTipoEChave(String tipo, String chave) {
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
}
