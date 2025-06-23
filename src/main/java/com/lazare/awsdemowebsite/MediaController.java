package com.lazare.awsdemowebsite;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<ObjectMetadata> getImageMetadata(@PathVariable String name){
        try{
            ObjectMetadata data = amazonS3.getObjectMetadata(bucketName, name);
            return ResponseEntity.ok(data);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/metadata/random")
    public ResponseEntity<Object> getImageMetadataRandom(){
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
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata);
            return ResponseEntity.ok("Uploaded: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed.");
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
