package dev.aira.lambda.report.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;

@ApplicationScoped
public class S3Producer {

    private final String bucket;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public S3Producer() {
        Region region = Region.of(System.getenv("aws.region"));
        this.bucket = System.getenv("aws.s3.bucket-report");
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
