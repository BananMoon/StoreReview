package com.review.storereview.service.cms;

import com.review.storereview.common.exception.ImageNotFoundException;
import com.review.storereview.dao.cms.Image;
import com.review.storereview.dto.response.ImageUploadResponseDto;
import com.review.storereview.repository.cms.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final S3ImageProcessService s3ImageProcessService;
    @Transactional
    public ImageUploadResponseDto uploadImage(MultipartFile multipartFile) {
        String fileName = s3ImageProcessService.createFileName(multipartFile);
        String uploadedImageUrl = s3ImageProcessService.uploadImage(multipartFile, fileName);
        Image savedImage = imageRepository.save(new Image(fileName, uploadedImageUrl));

        return new ImageUploadResponseDto(savedImage.getImageId(), savedImage.getReview().getReviewId(),
                uploadedImageUrl);
    }

    @Transactional
    public Long deleteImage(Long imageId) {
        Image image = Optional.ofNullable(imageRepository.findByImageId(imageId))
                .orElseThrow(ImageNotFoundException::new);

        s3ImageProcessService.deleteFile(image.getFileName());
        imageRepository.deleteById(image.getImageId());
        return image.getImageId();
    }
    @Transactional
    public void deleteAllImages(Long reviewId) {
        List<Image> images = Optional.ofNullable(imageRepository.findAllByReviewId(reviewId))
                .orElseThrow(ImageNotFoundException::new);
        // S3 이미지 삭제
        for(Image img : images) {
            s3ImageProcessService.deleteFile(img.getFileName());
        }
        // 테이블 데이터 제거
        imageRepository.deleteAllById(images.stream().map(Image::getImageId).collect(Collectors.toList()));
    }
}
