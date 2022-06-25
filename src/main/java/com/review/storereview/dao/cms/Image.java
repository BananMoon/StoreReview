package com.review.storereview.dao.cms;

import com.review.storereview.common.exception.ImageNotFoundException;
import com.review.storereview.common.utils.CryptUtils;
import com.review.storereview.dao.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Table(name="IMAGE")
@Getter
@Entity
@NoArgsConstructor
public class Image extends BaseTimeEntity {
    @Id
    @Column(name = "IMAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(name= "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Setter
    @Column(name="REVIEW_ID")
    private Long reviewId;

    private Image(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
    public static Image createImage(String fileName, String fileUrl) {
        return new Image(fileName, fileUrl);
    }

    public static void insertReviewId(List<Image> imgs, Long reviewId) {
        if (isEmptydoubleCheck(imgs)) throw new ImageNotFoundException();
        for (Image img : imgs) {
            img.setReviewId(reviewId);
        }
    }
    public static List<String> getImageUrls(List<Image> images) {
        List<String> encodedImgUrls = new ArrayList<>();
        if (!isEmptydoubleCheck(images)) {
            for (Image img : images) {
                encodedImgUrls.add(CryptUtils.Base64Encoding(img.getFileUrl()));
            }
        }
        return encodedImgUrls;
    }

    public static void deleteImage(List<Image> images) {
        if (isEmptydoubleCheck(images)) throw new ImageNotFoundException();
        for (Image img : images) {
            img.setReviewId(null);
        }
    }

    /**
     * 프론트로부터 혹은 DB로부터 전달된 객체가 비었는지 체크하기 위해
     * null인 경우와 빈 배열일 경우를 동시에 체크한다.
     */
    private static boolean isEmptydoubleCheck(List<?> objects) {
        if (!Objects.isNull(objects))
            if (!objects.isEmpty()) {
                return false;
            }
        return true;
    }
}
