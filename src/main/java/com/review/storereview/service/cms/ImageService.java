package com.review.storereview.service.cms;

import com.review.storereview.common.exception.ImageNotFoundException;
import com.review.storereview.dao.cms.Image;
import com.review.storereview.dto.response.ImageUploadResponseDto;
import com.review.storereview.repository.cms.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
/** Class       : ImageService (Service)
 *  Author      : 문 윤 지
 *  Description : Image 관련 요청 시 처리하는 서비스 Layer
 *  History     : [2022-06-26] Create
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3ImageProcessService s3ImageProcessService;

    @Transactional(readOnly = true)
    public List<String> listAll(Long reviewId) {
        List<Image> images = imageRepository.findAllByReviewId(reviewId);
        if (!Objects.isNull(images))
            if (!images.isEmpty()) {
                throw new ImageNotFoundException();
            }
        List<String> fileUrls = new ArrayList<>();
        for (Image image : images) {
            fileUrls.add(image.getFileUrl());
        }
        return fileUrls;
    }

    public ImageUploadResponseDto uploadImage(MultipartFile multipartFile) {
        String fileName = s3ImageProcessService.createFileName(multipartFile);
        String uploadedImageUrl = s3ImageProcessService.uploadImage(multipartFile, fileName);
        Image savedImage = imageRepository.save(Image.createImage(fileName, uploadedImageUrl));

        return new ImageUploadResponseDto(savedImage.getImageId(), savedImage.getReviewId(), uploadedImageUrl);
    }

    public Long deleteImage(Long imageId) {
        Image image = Optional.ofNullable(imageRepository.findByImageId(imageId))
                .orElseThrow(ImageNotFoundException::new);

        Image.deleteOne(image);
        imageRepository.deleteById(image.getImageId());
        return image.getImageId();
    }

    public void deleteAllImages(Long reviewId) {
        List<Image> images = Optional.ofNullable(imageRepository.findAllByReviewId(reviewId))
                .orElseThrow(ImageNotFoundException::new);
        for(Image img : images) {
            Image.deleteOne(img);
        }
        // 테이블 데이터 제거
        imageRepository.deleteAllById(images.stream().map(Image::getImageId).collect(Collectors.toList()));
    }

    public void clearUnusedImagesInDBAndS3for3weeks(Date now) {
        List<Image> imgsWithReviewIdIsNull = imageRepository.findAllByReviewId(null);
        List<Long> imgIds = new ArrayList<>();

        if (!Objects.isNull(imgsWithReviewIdIsNull)) {
            for (Image img : imgsWithReviewIdIsNull) {
                if (isUnusedFor3weeks(img.getUpdatedAt())) {
                    imgIds.add(img.getImageId());
                    s3ImageProcessService.deleteFile(img.getFileName());    // s3저장소에서 제거
                }
            }
        }

        imageRepository.deleteAllByImageId(imgIds);
    }

    private boolean isUnusedFor3weeks(LocalDateTime updatedAt) {
        LocalDateTime threeWeeksFromUpdated = updatedAt.plusWeeks(3L);
        return threeWeeksFromUpdated.isBefore(LocalDateTime.now()) || threeWeeksFromUpdated.isEqual(LocalDateTime.now()); // 현재거나 과거이면 true
    }
}