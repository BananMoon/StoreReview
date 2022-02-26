package com.review.storereview.dto.request;

import lombok.Getter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.lang.Nullable;

import java.io.InputStream;
/**
 *  테스트중 (only 로컬)
 * Class       : FileInfoDto
 * Author      : 문 윤 지
 * Description : MultipartFile을 Dto로 받는 Request DTO
 * History     : [2022-02-14] - 문 윤 지 - Class Create
 */
@Getter
public class FileInfoDto {
    @Nullable
    private String originalFileName;
    @Nullable
    private long size;
    @Nullable
    private String contentType;
    @Nullable
    private InputStreamResource inputStream;

    public FileInfoDto(String originalFileName, long size, String contentType, InputStreamResource inputStream) {
        this.originalFileName = originalFileName;
        this.size = size;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }
}
