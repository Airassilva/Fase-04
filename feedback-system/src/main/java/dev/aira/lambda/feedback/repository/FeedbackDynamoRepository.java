package dev.aira.lambda.feedback.repository;

import dev.aira.lambda.feedback.domain.Feedback;
import dev.aira.lambda.feedback.exceptions.PersistenceException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@ApplicationScoped
public class FeedbackDynamoRepository implements FeedbackRepository{

    private static final TableSchema<Feedback> SCHEMA = TableSchema.fromBean(Feedback.class);
    private final DynamoDbTable<Feedback> table;

    public FeedbackDynamoRepository(
            DynamoDbEnhancedClient enhancedClient,
            @ConfigProperty(name = "feedback.table.name")
            String tableName) {

        this.table = enhancedClient.table(tableName, SCHEMA);
    }

    @Override
    public void save(Feedback feedback) {
        try {
            table.putItem(feedback);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }
}
