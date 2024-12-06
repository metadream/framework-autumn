package com.arraywork.springforce.oss;

import java.io.InputStream;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;

/**
 * Minio Object Storage Service
 * Compatible with Amazon S3 (Includes Cloudflare R2)
 *
 * minio.endpoint = https://xxx.xxxxx.xxx
 * minio.accessKey = xxxxxxxxxxxxxxxx
 * minio.secretKey = yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy
 * minio.bucket = photo
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/09/18
 */
public class MinioService {

    private final String bucket;
    private final MinioClient client;

    public MinioService(String endpoint, String accessKey, String secretKey) {
        this(endpoint, accessKey, secretKey, null);
    }

    public MinioService(String endpoint, String accessKey, String secretKey, String bucket) {
        this.bucket = bucket;
        client = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }

    /** Put object with file path */
    public ObjectWriteResponse putObject(String object, String filename) {
        return putObject(bucket, object, filename);
    }

    public ObjectWriteResponse putObject(String bucket, String object, String filename) {
        Optional<MediaType> optional = MediaTypeFactory.getMediaType(filename);
        MediaType mediaType = optional.orElse(MediaType.APPLICATION_OCTET_STREAM);
        try {
            return client.uploadObject(UploadObjectArgs.builder()
                .bucket(bucket).object(object).filename(filename)
                .contentType(mediaType.toString())
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Put object with multipart file */
    public ObjectWriteResponse putObject(String object, MultipartFile file) {
        return putObject(bucket, object, file);
    }

    public ObjectWriteResponse putObject(String bucket, String object, MultipartFile file) {
        try {
            InputStream stream = file.getInputStream();
            return client.putObject(PutObjectArgs.builder()
                .bucket(bucket).object(object)
                .stream(stream, stream.available(), -1)
                .contentType(file.getContentType())
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Put object with input stream (unable to get ContentType) */
    public ObjectWriteResponse putObject(String object, InputStream stream) {
        return putObject(bucket, object, stream);
    }

    public ObjectWriteResponse putObject(String bucket, String object, InputStream stream) {
        try {
            return client.putObject(PutObjectArgs.builder()
                .bucket(bucket).object(object)
                .stream(stream, stream.available(), -1)
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Copying objects within the same bucket */
    public ObjectWriteResponse copyObject(String source, String destination) {
        return copyObject(bucket, source, destination);
    }

    public ObjectWriteResponse copyObject(String bucket, String source, String destination) {
        try {
            return client.copyObject(CopyObjectArgs.builder()
                .bucket(bucket).object(destination)
                .source(CopySource.builder()
                    .bucket(bucket).object(source)
                    .build())
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Delete object */
    public void deleteObject(String object) {
        deleteObject(bucket, object);
    }

    public void deleteObject(String bucket, String object) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket).object(object)
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}