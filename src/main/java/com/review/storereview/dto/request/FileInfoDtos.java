package com.review.storereview.dto.request;

import com.review.storereview.dto.response.ReviewFindResponseDto;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
/**
 * 테스트중 (only 로컬)
 */
@Getter
public class FileInfoDtos {
        @Nullable
        private List<FileInfoDto> fileInfoDtos;

        public FileInfoDtos(int size) {
            fileInfoDtos = new ArrayList<>(size); // 초기화
        }

        public void addFileInfoDto(FileInfoDto fileInfoDto)
        {
            this.fileInfoDtos.add(fileInfoDto);
        }
 }

