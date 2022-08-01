package io.openvidu.server.recording.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름
    @Value("${cloud.aws.s3.baseUrl}")
    public String baseUrl;  // S3 버킷 주소

    // 이미지 업로드
    public String upload(File file, String dirName) {
        // 1. S3에 업로드할 파일 이름 생성
        String fileName = createFileName(file.getName(), dirName);
        // 2. File S3에 업로드
        s3Upload(fileName, file);
        // 3. S3에 업로드된 이미지 URL 가져오기
        return getFileUrl(fileName);
    }

    // S3에 업로드된 이미지 파일 URL 가져오기
    private String getFileUrl(String fileName) {
        // 버킷과 네임과 파일이름으로 업로드된 이미지 파일 URL 가져오기
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // S3에 업로드될 이미지 파일이름 생성
    private String createFileName(String originalFilename, String dirName) {
        // 랜덤한 이미지파일 이름 생성
        return dirName + "/" + UUID.randomUUID() + originalFilename;
    }
    public String getOriginalFileName(String fileUrl, String dirName) {
        // 원본 파일 이름
        return getFileName(fileUrl).replace(dirName+"/","").substring(36);
    }
    public String getFileName(String fileUrl) {
        // 원본 파일 이름
        return fileUrl.replace(baseUrl, "");
    }

    // S3에 이미지 업로드
    private void s3Upload(String fileName, File file) {
        // 파일 객체 생성 후 S3에 버킷 업로드
        //new PutObjectRequest(bucket, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, file)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    // 업로드된 S3 파일 삭제
    public void deleteFromS3(String source) {
        System.out.println(source);
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, source));
    }
}
