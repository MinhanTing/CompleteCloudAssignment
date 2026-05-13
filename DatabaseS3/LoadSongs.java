// Adapted from MoviesLoadData.java
// Original source: AWS DynamoDB Developer Guide Java samples
// https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Java.html
// Changes made: removed local endpoint, changed table name to "music",
//               changed JSON field extraction to match music schema
//               (artist, title, year, album, image_url instead of year/title/info)

package com.amazonaws.samples.DatabaseS3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Iterator;

public class LoadSongs {

    public static void main(String[] args) throws Exception {


        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("music");


        JsonParser parser = new JsonFactory().createParser(new File("2026a2_songs.json"));
        JsonNode rootNode = new ObjectMapper().readTree(parser);


        JsonNode songsArray = rootNode.path("songs");
        Iterator<JsonNode> iter = songsArray.iterator();

        int successCount = 0;
        int failCount = 0;

        while (iter.hasNext()) {
            ObjectNode currentNode = (ObjectNode) iter.next();

            // Adapted from MoviesLoadData.java - changed fields to match music schema
            String artist   = currentNode.path("artist").asText();
            String title    = currentNode.path("title").asText();
            String year     = currentNode.path("year").asText();
            String album    = currentNode.path("album").asText();
            String imageUrl = currentNode.path("img_url").asText();

            try {

                table.putItem(new Item()
                        .withPrimaryKey("artist", artist, "title", title + "|" + year)
                        .withString("year", year)
                        .withString("album", album)
                        .withString("img_url", imageUrl));

                System.out.println("Added song: " + artist + " - " + title);
                successCount++;

            } catch (Exception e) {
                System.err.println("Failed to add: " + artist + " - " + title);
                System.err.println(e.getMessage());
                failCount++;
            }
        }

        parser.close();
        System.out.println("-----------------------------------");
        System.out.println("Load complete.");
        System.out.println("Successfully added: " + successCount + " songs");
        System.out.println("Failed:             " + failCount + " songs");
    }
}
