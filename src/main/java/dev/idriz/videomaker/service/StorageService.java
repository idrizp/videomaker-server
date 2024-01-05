package dev.idriz.videomaker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class StorageService {

    private final String awsBucketName;
    private final S3Client s3Client;

    public StorageService(
            final @Value("${app.aws_bucket_name}") String awsBucketName,
            final S3Client s3Client
    ) {
        this.awsBucketName = awsBucketName;
        this.s3Client = s3Client;
    }

    /**
     * Uploads a file to S3 and deletes it from the local filesystem
     *
     * @param path the <b>absolute</b> path of the file to upload
     * @return a CompletableFuture that will complete when the file has been uploaded and deleted with the URL of the file
     */
    public CompletableFuture<String> uploadAndDelete(final String path) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalStateException("File does not exist: " + path);
            }
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(awsBucketName)
                    .key(file.getName())
                    .build(), RequestBody.fromFile(file));
            file.delete();

            return s3Client.utilities().getUrl(builder -> builder.bucket(awsBucketName).key(file.getName())).toExternalForm();
        });
    }

    /**
     * Downloads a file from S3
     *
     * @param fileName the name of the file to download
     * @return a CompletableFuture that will complete with the downloaded file
     */
    public CompletableFuture<File> download(final String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            File file;
            try {
                file = File.createTempFile(fileName, fileName.substring(fileName.lastIndexOf(".")), new File("/tmp"));
                s3Client.getObject(GetObjectRequest.builder()
                        .bucket(awsBucketName)
                        .key(fileName)
                        .build(), ResponseTransformer.toFile(file));
                return file;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
