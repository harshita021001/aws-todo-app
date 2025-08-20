package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

public class GetToDosHandler implements RequestHandler<Map<String, Object>, List<Map<String, String>>> {

    private static final String TABLE_NAME = "ToDoTable";
    private final DynamoDbClient dynamoDb = DynamoDbClient.create();

    @Override
    public List<Map<String, String>> handleRequest(Map<String, Object> input, Context context) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .build();

        ScanResponse scanResponse = dynamoDb.scan(scanRequest);

        List<Map<String, String>> results = new ArrayList<>();
        for (Map<String, AttributeValue> item : scanResponse.items()) {
            Map<String, String> task = new HashMap<>();
            task.put("id", item.get("id").s());
            task.put("task", item.get("task").s());
            results.add(task);
        }

        return results;
    }
}
