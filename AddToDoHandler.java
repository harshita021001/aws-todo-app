package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddToDoHandler implements RequestHandler<Map<String, Object>, String> {

    private static final String TABLE_NAME = "ToDoTable";
    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        String bodyString = (String) input.get("body");
        Map<String, Object> body;
        try {
            body = objectMapper.readValue(bodyString, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse input body JSON", e);
        }

        String task = (String) body.get("task");
        if (task == null || task.isEmpty()) {
            throw new IllegalArgumentException("task is required");
        }

        String id = UUID.randomUUID().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id).build());
        item.put("task", AttributeValue.builder().s(task).build());

        PutItemRequest putReq = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDb.putItem(putReq);

        return "Added to-do item with id: " + id;
    }
}
