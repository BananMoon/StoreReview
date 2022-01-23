package com.review.storereview.dao.cms;

import com.review.storereview.common.utils.StringListConverter;
import com.review.storereview.dao.BaseTimeEntity;
import com.review.storereview.dao.cust.User;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name="REVIEW")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 테스트할 경우 PUBLIC으로 설정
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="REVIEW_ID")
    private Long reviewId;

    @ManyToOne(fetch=FetchType.LAZY)      // Review To User
    @JoinColumn(name="SAID")
    private User said;

    @ManyToOne(fetch=FetchType.LAZY)      // Review To User
    @JoinColumn(name="SUID")
    private User suid;

    @Column(name="PLACE_ID", length = 20)
    private String placeId;

    @Column(name = "STARS", nullable = false)
    private Integer stars;

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "IMG_URL", length = 45)
    @Convert(converter = StringListConverter.class)
    private List<String> imgUrl;


    @Builder
    public Review (User suid, User said, String placeId, Integer stars, String content, List<String> imgUrl) {
        this.suid = suid;
        this.said = said;
        this.placeId = placeId;
        this.stars = stars;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    // ReviewUpdateRequestDto에서 필요
    public Review(String content) {
        this.content = content;
    }

    public String getSuid() {
        return  String.valueOf(suid);
    }

    public void update(String content) {
        this.content = content;
    }
}
