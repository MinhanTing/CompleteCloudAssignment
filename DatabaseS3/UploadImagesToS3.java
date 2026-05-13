
package com.amazonaws.samples.DatabaseS3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UploadImagesToS3 {

    // Change this to your S3 bucket name
    //CREATE YOUR OWN UNIQUE NAME - PUT IT IN "PUT YOU OWN UNIQUE NAME HERE"
    private static final String BUCKET_NAME = "PUT YOU OWN UNIQUE NAME HERE";

    public static void main(String[] args) throws Exception {


        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();


        if (!s3Client.doesBucketExistV2(BUCKET_NAME)) {
            s3Client.createBucket(BUCKET_NAME);
            System.out.println("Created S3 bucket: " + BUCKET_NAME);
        } else {
            System.out.println("Bucket already exists: " + BUCKET_NAME);
        }


        JsonParser parser = new JsonFactory().createParser(new File("2026a2_songs.json"));
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        JsonNode songsArray = rootNode.path("songs");
        Iterator<JsonNode> iter = songsArray.iterator();


        Set<String> uploadedUrls = new HashSet<>();
        int successCount = 0;
        int skipCount = 0;
        int failCount = 0;

        while (iter.hasNext()) {
            ObjectNode currentNode = (ObjectNode) iter.next();
            String imageUrl = currentNode.path("img_url").asText();
            String artist   = currentNode.path("artist").asText();

            // Skip if already uploaded this URL
            if (uploadedUrls.contains(imageUrl)) {
                System.out.println("Skipping duplicate image for: " + artist);
                skipCount++;
                continue;
            }

            uploadedUrls.add(imageUrl);


            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            try {

                URL url = new URL(imageUrl);
                InputStream imageStream = url.openStream();


                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("image/jpeg");


                s3Client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, imageStream, metadata));

                System.out.println("Uploaded: " + fileName + " (artist: " + artist + ")");
                successCount++;
                imageStream.close();

            } catch (Exception e) {
                System.err.println("Failed to upload image for: " + artist + " | URL: " + imageUrl);
                System.err.println(e.getMessage());
                failCount++;
            }
        }

        parser.close();
        System.out.println("-----------------------------------");
        System.out.println("Upload complete.");
        System.out.println("Successfully uploaded: " + successCount + " images");
        System.out.println("Skipped (duplicates): " + skipCount);
        System.out.println("Failed:               " + failCount);
        System.out.println("Bucket name: " + BUCKET_NAME);
    }
}
