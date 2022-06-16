package com.review.storereview.dao.cms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Table(name="IMAGE")
@Getter
@Entity
@NoArgsConstructor
public class Image {
    @Id
    @Column(name = "IMAGE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long imageId;

    @Column(name= "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_URL")
    private String fileUrl;

    //@JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "REVIEW_ID")
    private Review review;

//    private LocalDateTime createdAt;

    public Image(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
