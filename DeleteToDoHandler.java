package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteToDoHandler implements RequestHandler<Map<String, String>, String> {

    private static final String TABLE_NAME = "ToDoTable";
    private final DynamoDbClient dynamoDb = DynamoDbClient.create();

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        String id = input.get("id");

        if (id == null) {
            return "Error: 'id' must be provided";
        }

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        try {
            dynamoDb.deleteItem(request);
            return "Task with id " + id + " deleted successfully";
        } catch (Exception e) {
            return "Error deleting task: " + e.getMessage();
        }
    }
}
