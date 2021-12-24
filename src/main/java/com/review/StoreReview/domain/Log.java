package com.review.StoreReview.domain;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name="API_LOG")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 테스트할 경우 PUBLIC으로 설정
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)    // Log to User
    @JoinColumn(name = "SUID")
    private User suid;

    @ManyToOne(fetch=FetchType.LAZY)    // Log to User
    @JoinColumn(name = "SAID")
    private User said;

    private LocalDateTime date;

    @Column(name="API_NAME", nullable = false, length = 20)
    private  String apiName;

    @Column(name="API_STATUS")
    private char apiStatus;

    @Column(name="API_DESC", length = 100)
    private  String apiDesc;

    @Column(name="PROCESS_TIME")
    private double processTime;

    @Builder
    public Log(User suid, User said, LocalDateTime date, String apiName, char apiStatus, String apiDesc, double processTime) {
        this.suid = suid;
        this.said = said;
        this.date = date;
        this.apiName = apiName;
        this.apiStatus = apiStatus;
        this.apiDesc = apiDesc;
        this.processTime = processTime;
    }
}
