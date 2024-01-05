package dev.idriz.videomaker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Configuration {


    @Bean
    AwsCredentials awsCredentials(final @Value("${secrets.aws_access_key}") String accessKey,
                                  final @Value("${secrets.aws_secret_key}") String secretKey) {
        return AwsBasicCredentials.create(accessKey, secretKey);
    }

    @Bean
    public S3Client s3Client(final AwsCredentials awsCredentials) {
        return S3Client.builder()
                .credentialsProvider(() -> awsCredentials)
                .build();
    }

}
