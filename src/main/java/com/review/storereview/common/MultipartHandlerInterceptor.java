package com.review.storereview.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.review.storereview.dto.request.FileInfoDto;
import com.review.storereview.dto.request.FileInfoDtos;
import com.review.storereview.dto.request.ReviewUploadWithFileRequestDto;
import com.review.storereview.dto.response.ReviewFindListResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * 테스트중 (only 로컬)
 */
@Component
public class MultipartHandlerInterceptor implements HandlerInterceptor {

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

//    public MultipartHandlerInterceptor(ObjectMapper mapper) {
//        this.mapper = mapper;
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle 출력됨");
//        System.out.println(request.getSession().getAttribute("imgFileList"));
        // 그냥 request의 파라미터 imgFileList는 null로 옴.
        //            System.out.println((request.getParameter("imgFileList")).getBytes());   // null..
        //            System.out.println((request.getParameter("imgFileList")).isEmpty());  // null
        System.out.println(request.getParameter("content"));
//        request.setAttribute("stars", null);
        Integer stars = Integer.valueOf(request.getParameter("stars"));
        ReviewUploadWithFileRequestDto requestDto = new ReviewUploadWithFileRequestDto(
                request.getParameter("placeId"),
                request.getParameter("contents"),
                stars
        );

        if (request instanceof MultipartHttpServletRequest) {
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes((MultipartHttpServletRequest) request));
            MultiValueMap<String, MultipartFile> multiFileMap = ((MultipartHttpServletRequest) request).getMultiFileMap();
            if (multiFileMap.isEmpty())
                System.out.println("multiFileMap이 비었음");

            System.out.println(multiFileMap.values().getClass());  // class java.util.LinkedHashMap$LinkedValues
            for (Object o : multiFileMap.keySet()) {    // imgFileList 가 key
                List<MultipartFile> multipartFiles = multiFileMap.get(o.toString());
                for (MultipartFile m : multipartFiles) {
                    System.out.println(m.getContentType()); // image/jpeg
                }
            }
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            List<MultipartFile> imgFiles = multipartHttpServletRequest.getFiles("imgFileList");

            // Dto로 변환
/*
            FileInfoDtos fileInfoDtos = new FileInfoDtos(imgFiles.size());
            FileInfoDto fileInfoDto;
            MultipartInputStreamFileResource multipartInputStreamFileResource;
            System.out.println("MultipartFIle 반복문 돌기 전");
            for (MultipartFile imgFile : imgFiles) {
                multipartInputStreamFileResource = new MultipartInputStreamFileResource(imgFile.getInputStream(), imgFile.getOriginalFilename());
                fileInfoDto = new FileInfoDto(
                        imgFile.getOriginalFilename(),
                        imgFile.getSize(),
                        imgFile.getContentType(),
                        multipartInputStreamFileResource
                );
                fileInfoDtos.addFileInfoDto(fileInfoDto);
            }
            System.out.println("fileInfoDtos 세팅하기 전");
            request.setAttribute("fileInfoDtos", fileInfoDtos);
*/

            System.out.println("MultipartFIle 반복문 돌기 전");
            FileInfoDtos fileInfoDtos = new FileInfoDtos(imgFiles.size());
            MultipartInputStreamFileResource multipartInputStreamFileResource;
            FileInfoDto fileInfoDto;
            for (MultipartFile imgFile : imgFiles) {
                multipartInputStreamFileResource = new MultipartInputStreamFileResource(imgFile.getInputStream(), imgFile.getOriginalFilename());
                fileInfoDto = new FileInfoDto(
                        imgFile.getOriginalFilename(),
                        imgFile.getSize(),
                        imgFile.getContentType(),
                        multipartInputStreamFileResource
                );
                fileInfoDtos.addFileInfoDto(fileInfoDto);
                request.setAttribute("fileInfoDto", fileInfoDto);

            }
//            String stringFileInfoDtos = mapper.writeValueAsString(fileInfoDtos);
            /*System.out.println("requestDto.setFileInfoDtos 세팅하기 전");
            requestDto.setFileInfoDtos(fileInfoDtos);
            StringBuilder stringJson = new StringBuilder();
            for (MultipartFile imgFile : imgFiles) {
                multipartInputStreamFileResource = new MultipartInputStreamFileResource(imgFile.getInputStream(), imgFile.getOriginalFilename());

//                stringJson.append("{").append(System.lineSeparator())
//                    .append("originalFileName : ").append(imgFile.getOriginalFilename()).append(System.lineSeparator())
//                        .append(", size : ").append(imgFile.getSize()).append(System.lineSeparator())
//                        .append(", contentType : ").append(imgFile.getContentType()).append(System.lineSeparator())
//                        .append(", inputStream : ").append(imgFile.getInputStream()).append(System.lineSeparator())
//                        .append("}");
            }*/
        }
        return true;
    }

    public class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // we do not want to generally read the whole stream into memory ...
        }
    }

}