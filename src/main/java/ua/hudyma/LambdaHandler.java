package ua.hudyma;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class LambdaHandler implements RequestStreamHandler {
    public static final String PATH_PARAMETERS = "pathParameters", ID = "id", DYNAMO_TABLE = "Users", STATUS_CODE = "statusCode";

    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        var writer = new OutputStreamWriter(output);
        var reader = new BufferedReader(new InputStreamReader(input));
        var parser = new JSONParser();
        var responseObject = new JSONObject();
        var responseBody = new JSONObject();

        var client = AmazonDynamoDBClientBuilder.defaultClient();
        var dynamoDB = new DynamoDB(client);

        int id;
        Item resItem = null;

        JSONObject reqObject = (JSONObject) parser.parse(reader);
        if (reqObject.get(PATH_PARAMETERS) != null) {
            var pathParam = (JSONObject) reqObject.get(PATH_PARAMETERS);
            if (pathParam.get(ID) != null) {
                id = Integer.parseInt((String) pathParam.get(ID));
                resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem(ID, id);
            }
        }
        if (resItem != null) {
            var user = new User(resItem.toJSON());
            responseBody.put("user", user);
            responseObject.put(STATUS_CODE, 200);
        } else {
            responseBody.put("message", "No items found");
            responseObject.put(STATUS_CODE, 404);
        }
        responseObject.put("body", responseBody.toString());
        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }
}
