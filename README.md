# StoreReview
리뷰 서비스를 제공하는 웹 사이트 개발 프로젝트입니다. 😊

> 2달 간의 개발 기록은 아래 링크의 Repository에 남겨있으며, 이후 필요한 부분을 개선시키는 작업은 현재 저의 Repository에서 진행 중입니다.
해당 README 또한 개선된 작업 상황을 작성 중에 있으며, 해결과제의 Back-End에는 본인의 작업 중심으로 작성하였습니다. 

~웹사이트 보러가기😋~ 아래의 시연 영상 참고해주세요! <br>
[Back-End repository](https://github.com/RedJunHee/StoreReview) 👈 해당 repository에서 Pull Request 기록을 볼 수 있습니다!<br>
[Front-End repository](https://github.com/ghtea/store-review-fe)

## 팀원 🧑🏻‍🤝‍🧑🏻
- Front-End : [박재현](https://github.com/ghtea)
- Back-End : [문윤지](https://github.com/BananMoon), [조준희](https://github.com/RedJunHee)

## 주요 기능 💃
- 이미지 업로드, 별점, 댓글, 로그인, 회원가입, 검색, 가게 정보 조회

## 기술 스택🔧

### Front-End
- **Library**
- react
  - redux
  - redux-saga
  - react-router-dom
- HTML5
- CSS, styled-component
- Javascript, Typescript

### Back-End
- **Server**
    - AWS EC2 (변경)
- **Language**
    - Java 8
- **FrameWork**
    - Spring Boot 2.5.x
    - Spring Security
- **Module**
    - Spring Data JPA
    - slf4j
- **Build Tool**
    - Gradle
- **DataBase**
    - MySQL
- **Cloud Service**
    - AWS RDS
    - AWS S3
    
### Cooperation Tool
- **버전 관리**
    - Git(Pull Request Feedback Cycle)
- **의견 공유 및 정리**
    - Discord
    - Notion
    - Figma (프로토타입)


## 해결 과제 🤹
<details>
<summary>Front-End</summary>
  
- [X] 회원가입, 로그인 직접 구현 
- [X] 지도 api 적용
- [X] redux-saga 로 비동기 작업 & 상태 관리
- [X] 이미지 업로드
- [X] 리뷰 코멘트 Form
- [X] 코멘트 Pagination
</details>

### Back-End
> 본인 작업 혹은 공동 작업에 대해 **bold** 표시하였습니다.
- [X] **웹 서버 구축** (EC2 서비스 사용)
- [X] **[api 규약 정의](https://docs.google.com/document/d/1JvENVWph2QBL9mxwRsd1sTEhiNriD3MX/edit?usp=sharing&ouid=116446426306038263641&rtpof=true&sd=true)**
- [X] DB 설계 & ERD 작성 (**로그 정보 위한 LOG 테이블, 리뷰 및 코멘트 정보 위한 REVIEW, COMMENT 테이블**)
- [X] 프로젝트 세팅
- [X] **모델 초기 정의 및 MySQL 연동**
- [X] **[일부 테스트 코드](https://github.com/BananMoon/StoreReview/tree/master/src/test) 작성**
- [X] 기능 구현
    - [X] Spring Security JWT 인증/인가 기능 구현
      - [X] 로그인 (Authenticate) JWT 발급
    - [X] **회원가입 **
      - ~Validator 기능 구현~
    - [X] Utils 기능 구현
      - AES256, BCrypt, **Base64** 인코딩 및 디코딩
      - **타입 컨버터 기능**
    - [X] **전역 예외 처리 핸들러 구현**
    - [X] **리뷰 조회 (가게의 전체 리뷰, 개별 리뷰)**
    - [X] **리뷰 작성** ~(➕이미지 파일 관련 작업) 👉 따로 분리~  
    - [X] **리뷰 수정** (리뷰 작성자 권한 필요)
    - [X] **리뷰 삭제** (리뷰 작성자 권한 필요)
    - [X] 리뷰 코멘트 조회 (리뷰에 달린 모든 코멘트 + Paging) 
    - [X] 리뷰 코멘트 작성 
    - [X] 리뷰 코멘트 수정 (리뷰 사용자 체크 후 수정 기능 수행)
---

**개선 작업**  ([관련 PR](https://github.com/BananMoon/StoreReview/pulls?q=is%3Apr+is%3Aclosed))
- [X] 리뷰 관련 요청 API에서 이미지 관련 작업을 분리하였습니다.
  - [X] 이미지 Entity, MVC layer 추가 생성
  - [ ] 테스트코드 추가 생성
  - [X] 기존 리뷰 Entity, MVC layer 수정
  - [ ] 기존 테스트코드 수정


- [ ] 스케줄러 기능 추가 : 서비스 상 업로드되어있지 않은 (revewId 필드 ==`null`) 이미지 데이터에 대해 삭제 작업

### 동작 모습
```
회원가입 진행 👉 로그인 진행 👉 지도 '현재 위치' 기능 확인 👉 등록된 리뷰글에 코멘트 작성 👉 
리뷰글 작성 및 이미지 선택-삭제-재선택 후 리뷰 업로드 👉 로그아웃 후 상태
```
- 아래 영상에는 중심 기능 위주로 테스트하였습니다.
<p align="center">
  <img src="https://user-images.githubusercontent.com/66311276/155641229-98ba8550-d04e-47b8-adc0-c69b25a59e68.gif" width = 750, height=450>
</p>
