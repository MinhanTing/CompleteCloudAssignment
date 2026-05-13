// Adapted from MoviesCreateTable.java and MoviesItemOps01.java
// Original source: AWS DynamoDB Developer Guide Java samples
// https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Java.html
// Changes made: removed local endpoint, changed table schema to login table,
//               added 10 pre-defined users using putItem pattern from MoviesItemOps01.java

package com.amazonaws.samples.DatabaseS3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.Arrays;

public class CreateLoginTable {

    public static void main(String[] args) throws Exception {


        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        String tableName = "login";

        try {
            System.out.println("Creating login table, please wait...");


            Table table = dynamoDB.createTable(
                    tableName,
                    Arrays.asList(
                            new KeySchemaElement("email", KeyType.HASH)   // Partition key
                    ),
                    Arrays.asList(
                            new AttributeDefinition("email", ScalarAttributeType.S)
                    ),
                    new ProvisionedThroughput(5L, 5L)
            );

            table.waitForActive();
            System.out.println("Login table created. Status: " + table.getDescription().getTableStatus());

            String[][] users = {
                    {"s3986753@student.rmit.edu.au", "Ling Liang",     "Liang1234"},
                    {"s3896587@student.rmit.edu.au", "Jad Nehme",      "Nehme2345"},
                    {"s3915435@student.rmit.edu.au", "Yueying Chen",   "Chen3456"},
                    {"s3924635@student.rmit.edu.au", "Jay Clark",      "Clark4567"},
                    {"s3932634@student.rmit.edu.au", "Elijah Turner",  "Turner5678"},
                    {"s3947234@student.rmit.edu.au", "Layla Harris",   "Harris6789"},
                    {"s3954123@student.rmit.edu.au", "Mason Wright",   "Wright7890"},
                    {"s3961342@student.rmit.edu.au", "Olivia Scott",   "Scott8901"},
                    {"s3972341@student.rmit.edu.au", "Noah Adams",     "Adams9012"},
                    {"s3984521@student.rmit.edu.au", "Emma Baker",     "Baker0123"}
            };

            for (String[] user : users) {
                table.putItem(new Item()
                        .withPrimaryKey("email", user[0])
                        .withString("user_name", user[1])
                        .withString("password", user[2]));
                System.out.println("Added user: " + user[0]);
            }

            System.out.println("All 10 users added successfully.");

        } catch (Exception e) {
            System.err.println("Unable to create login table: ");
            System.err.println(e.getMessage());
        }
    }
}
