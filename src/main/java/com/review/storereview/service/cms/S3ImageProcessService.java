package com.review.storereview.service.cms;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.review.storereview.common.exception.ParamValidationException;
import com.review.storereview.service.BaseImageProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class S3ImageProcessService implements BaseImageProcessService {

    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        // AWSCredentials 생성
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        // S3 Client 생성
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    /**
     * 파일 이름을 안겹치게 생성한다.
     * @param multipartFile
     * @return 업로드한 이미지파일의 url 반환
     */
    public String createFileName(MultipartFile multipartFile) {
        // 1.  중복아닌 이름 생성  ex) {UUID}_{파일명} +
        String fileName = getFileExtension(multipartFile.getOriginalFilename());
        String randomFileName = UUID.randomUUID() + "_" + fileName;
        // 2. ObjectMetadata로 변환 및 S3 업로드
        return randomFileName;
    }

    /**
     * 실제 s3에 이미지를 업로드한 후 접근 가능한 주소를 반환한다.
     * @param uploadFile
     * @param fileName
     */
    public String uploadImage(MultipartFile uploadFile, String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(uploadFile.getSize());
        objectMetadata.setContentType(uploadFile.getContentType());
        try(InputStream inputStream = uploadFile.getInputStream()) {
            s3Client.putObject(
                    new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)  // {S3 주소}/{UUID}_{파일명} 경로로 파일 업로드
                            .withCannedAcl(CannedAccessControlList.PublicRead)  // 누구나 파일 읽기 가능
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }
        // TODO url을 전달할 지 파일명을 전달할지 결정해야함 with 프론트
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    /**
     * 저장소에 있는 이미지파일을 삭제한다.
     * @param fileName
     */
    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            Map<String, String> parameterErrorMsg = new HashMap<>();
            parameterErrorMsg.put(fileName, "잘못된 형식의 파일입니다.");
            throw new ParamValidationException(parameterErrorMsg);
        }
    }
}