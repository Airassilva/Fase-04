package dev.aira.lambda.report.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;

@ApplicationScoped
public class S3Producer {

    private final String bucket;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Inject
    public S3Producer( @ConfigProperty(name = "aws.s3.bucket-report") String bucket) {
        Region region = DefaultAwsRegionProviderChain.builder().build().getRegion();
        this.bucket = bucket;
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
        this.presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    public String uploadRelatorio(byte[] pdfBytes, String nomeArquivo) {
            s3Client.putObject(builder -> builder
                            .bucket(bucket)
                            .key(nomeArquivo)
                            .contentType("application/pdf"),
                    RequestBody.fromBytes(pdfBytes)
            );
            return nomeArquivo;
    }

    public String gerarPresignedUrl(String nomeArquivo) {
        return presigner.presignGetObject(p -> p
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(g -> g
                        .bucket(bucket)
                        .key(nomeArquivo)
                )

        ).url().toString();
    }
}
