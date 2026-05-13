// Adapted from MoviesCreateTable.java
// Original source: AWS DynamoDB Developer Guide Java samples
// https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Java.html
// Changes made: removed local endpoint, changed table name to "music",
//               changed key schema to artist (HASH) + title (RANGE),
//               added one LSI (artist-album-index) and one GSI (year-title-index)
//               as required by the assignment specification

package com.amazonaws.samples.DatabaseS3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class CreateMusicTable {

    public static void main(String[] args) throws Exception {

        // Adapted from MoviesCreateTable.java
        // Removed local endpoint configuration; using real AWS region instead
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        try {
            System.out.println("Creating music table, please wait...");



            CreateTableRequest request = new CreateTableRequest()
                    .withTableName("music")
                    .withKeySchema(
                            new KeySchemaElement("artist", KeyType.HASH),   // Partition key
                            new KeySchemaElement("title",  KeyType.RANGE)   // Sort key
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("artist", ScalarAttributeType.S),
                            new AttributeDefinition("title",  ScalarAttributeType.S),
                            new AttributeDefinition("year",   ScalarAttributeType.S),
                            new AttributeDefinition("album",  ScalarAttributeType.S)
                    )

                    .withLocalSecondaryIndexes(
                            new LocalSecondaryIndex()
                                    .withIndexName("artist-album-index")
                                    .withKeySchema(
                                            new KeySchemaElement("artist", KeyType.HASH),
                                            new KeySchemaElement("album",  KeyType.RANGE)
                                    )
                                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                    )

                    .withGlobalSecondaryIndexes(
                            new GlobalSecondaryIndex()
                                    .withIndexName("year-title-index")
                                    .withKeySchema(
                                            new KeySchemaElement("year",  KeyType.HASH),
                                            new KeySchemaElement("title", KeyType.RANGE)
                                    )
                                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

            Table table = dynamoDB.createTable(request);
            table.waitForActive();
            System.out.println("Music table created. Status: " + table.getDescription().getTableStatus());

        } catch (Exception e) {
            System.err.println("Unable to create music table: ");
            System.err.println(e.getMessage());
        }
    }
}
