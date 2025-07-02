package com.lazare.awsdemowebsite.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.lazare.awsdemowebsite.service.NotificationQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Random;


@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {


    private final AmazonS3 amazonS3;
    private final String bucketName = "lazare-g-bucket";
    private final Random random = new Random();
    private final NotificationQueueService notificationQueueService;

    @GetMapping(value = "/{name}")
    public ResponseEntity<byte[]> getImage(@PathVariable String name) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, name);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] imageBytes = inputStream.readAllBytes();
            inputStream.close();

            return ResponseEntity.ok()
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/metadata/{name}")
    public ResponseEntity<ObjectMetadata> getImageMetadata(@PathVariable String name) {
        try {
            ObjectMetadata data = amazonS3.getObjectMetadata(bucketName, name);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/metadata/random")
    public ResponseEntity<Object> getImageMetadataRandom() {
        ObjectListing objects = amazonS3.listObjects(bucketName);
        List<ObjectMetadata> metaDataEntries = objects.getObjectSummaries()
                .stream()
                .map(s3ObjectSummary -> {
                    String name = s3ObjectSummary.getKey();
                    ObjectMetadata objectMetaData;
                    try {
                        objectMetaData = amazonS3.getObjectMetadata(bucketName, name);
                    } catch (Exception e) {
                        return null;
                    }
                    return objectMetaData;
                })
                .filter(Objects::nonNull)
                .toList();

        int randomIndex = random.nextInt(0, metaDataEntries.size());
        ObjectMetadata res = metaDataEntries.get(randomIndex);
        return res != null ? ResponseEntity.ok(res) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        long size = file.getSize();
        String contentType = file.getContentType();
        String extension = "";

        if (filename != null && filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf('.') + 1);
        }

        try {
            // 1) Upload to S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);
            metadata.setContentType(contentType);
            amazonS3.putObject(bucketName, filename, file.getInputStream(), metadata);

            // 2) Build the notification message
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/media/")
                    .path(URLEncoder.encode(filename, StandardCharsets.UTF_8))
                    .toUriString();

            StringBuilder body = new StringBuilder();
            body.append("An image has been uploaded!\n\n")
                    .append("• Name: ").append(filename).append("\n")
                    .append("• Extension: ").append(extension).append("\n")
                    .append("• Size: ").append(size).append(" bytes\n\n")
                    .append("Download it here: ").append(downloadUrl);

            // 3) Send it to your SQS queue (to be picked up by your scheduler)
            notificationQueueService.notifyMessage(body.toString());

            return ResponseEntity.ok("Uploaded: " + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed.");
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteImage(@PathVariable String name) {
        try {
            amazonS3.deleteObject(bucketName, name);
            return ResponseEntity.ok("Deleted: " + name);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion failed.");
        }
    }


}
