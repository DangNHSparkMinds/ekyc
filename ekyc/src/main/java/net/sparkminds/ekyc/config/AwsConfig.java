package net.sparkminds.ekyc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    private StaticCredentialsProvider awsCredentials() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey));
    }

    @Bean
    public RekognitionClient rekognitionClient() {
        return RekognitionClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(awsCredentials())
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(awsCredentials())
                .build();
    }
}
