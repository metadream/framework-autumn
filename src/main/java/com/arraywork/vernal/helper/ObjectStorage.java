package com.arraywork.vernal.helper;

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
 * Object Storage Client
 * Compatible with Amazon S3 (Includes Cloudflare R2)
 * (Depends on io.minio)
 *
 * <pre>
 *   minio.endpoint = https://xxx.xxxxx.xxx
 *   minio.accessKey = xxxxxxxxxxxxxxxx
 *   minio.secretKey = yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy
 *   minio.bucket = photo
 * </pre>
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/09/18
 */
public class ObjectStorage {

    private final String bucket;
    private final MinioClient client;

    public ObjectStorage(String endpoint, String accessKey, String secretKey) {
        this(endpoint, accessKey, secretKey, null);
    }

    public ObjectStorage(String endpoint, String accessKey, String secretKey, String bucket) {
        this.bucket = bucket;
        client = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }

    /** Put object with file path */
    public ObjectWriteResponse put(String object, String filename) {
        return put(bucket, object, filename);
    }

    public ObjectWriteResponse put(String bucket, String object, String filename) {
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
    public ObjectWriteResponse put(String object, MultipartFile file) {
        return put(bucket, object, file);
    }

    public ObjectWriteResponse put(String bucket, String object, MultipartFile file) {
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
    public ObjectWriteResponse put(String object, InputStream stream) {
        return put(bucket, object, stream);
    }

    public ObjectWriteResponse put(String bucket, String object, InputStream stream) {
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
    public ObjectWriteResponse copy(String source, String destination) {
        return copy(bucket, source, destination);
    }

    public ObjectWriteResponse copy(String bucket, String source, String destination) {
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
    public void delete(String object) {
        delete(bucket, object);
    }

    public void delete(String bucket, String object) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket).object(object)
                .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}