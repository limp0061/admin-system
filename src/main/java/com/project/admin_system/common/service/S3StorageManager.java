package com.project.admin_system.common.service;


import java.io.IOException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageManager {

    private static final int EXPIRE_MINUTES = 15;

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${cloudflare.r2.bucket}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}")
    private String url;

    public void upload(String key, MultipartFile file) {
        upload(key, file, "public, max-age=31536000");
    }

    public void upload(String key, MultipartFile file, String cacheControl) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .cacheControl(cacheControl) // 기본은 캐싱 처리하고 보안이 필요하면 no-store
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("[S3 업로드] key={}", key);
        } catch (IOException | S3Exception e) {
            log.error("[S3 업로드 실패] key={} | {}", key, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);

            log.info("[S3 삭제] key={}", key);
        } catch (S3Exception e) {
            log.error("[S3 삭제 실패] key={} | {}", key, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteFiles(List<String> keys) {
        if (keys.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objects = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objects).build())
                .build();

        try {
            s3Client.deleteObjects(request);
            log.info("[S3 배치 삭제] {}개 삭제", keys.size());
        } catch (S3Exception e) {
            log.error("[S3 배치 삭제 실패] | {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getUrl(String key) {
        return String.format("%s/%s", url, key);
    }

    public String getPresignedUrl(String key) {

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(EXPIRE_MINUTES))
                .getObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public void moveObject(String sourceKey, String destinationKey) {
        try {
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(destinationKey)
                    .build();
            s3Client.copyObject(copyRequest);
            deleteFile(sourceKey);
            log.info("[S3 이동] {} → {}", sourceKey, destinationKey);
        } catch (S3Exception e) {
            log.error("[S3 이동 실패] {} → {} | {}", sourceKey, destinationKey, e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
