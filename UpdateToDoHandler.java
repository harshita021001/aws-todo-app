package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;

import java.util.HashMap;
import java.util.Map;

public class UpdateToDoHandler implements RequestHandler<Map<String, String>, String> {

    private static final String TABLE_NAME = "ToDoTable";
    private final DynamoDbClient dynamoDb = DynamoDbClient.create();

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        String id = input.get("id");
        String newTask = input.get("task");

        if (id == null || newTask == null || newTask.isEmpty()) {
            return "Error: 'id' and non-empty 'task' must be provided";
        }

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());

        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#T", "task");

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":newTask", AttributeValue.builder().s(newTask).build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .updateExpression("SET #T = :newTask")
                .expressionAttributeNames(expressionNames)
                .expressionAttributeValues(expressionValues)
                .returnValues(ReturnValue.UPDATED_NEW)
                .build();

        try {
            dynamoDb.updateItem(request);
            return "Task with id " + id + " updated successfully";
        } catch (Exception e) {
            return "Error updating task: " + e.getMessage();
        }
    }
}
