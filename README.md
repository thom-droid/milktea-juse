# JUSE

**JU**nior에서 **SE**nior가 될때까지, 그리고 그 뒤에도.

<img src='./client/public/JUSE.png'>

## 👨‍👧프로젝트 소개

### 개발자를 위한 프로젝트/스터디 모집 플랫폼, [JUSE](https://chicken-milktea-juse.com/)

좋은 소프트웨어 엔지니어가 되기 위해서는 주니어부터 시니어가 되기까지, 또한 시니어가 되어서도 학습과 고민을 멈추어선 안되겠죠.

그러나 방대한 지식을 혼자 습득 하는 것은 어려울 수 있습니다. 같은 목적을 가진 사람들과 함께 한다면 더 빨리, 많이 배울 수 있다고 생각한 것이 JUSE의 출발점입니다.
<br/>

> 개발 기간: 2022.09.07 - 2022.10.12 (5주, 팀 프로젝트) </br>
> 추가 개발: 2022.11.01 - 현재 (개인 프로젝트)
</br>

v1는 프로젝트 기간 내에 프론트, 백엔드 팀이 함께 작업한 버전입니다.
</br>
v2는 프로젝트 기간 이후 개인적으로 추가 작업을 실시한 버전입니다.


## 배포

- 현재 최신 버전: v2.1.5
- 배포 버전 별 내역은 [release note](https://github.com/thom-droid/milktea-juse/wiki/Release-Note) 에서 확인할 수 있습니다.

## 서버 구성

### v1
<details>
<summary> 상세보기 </summary>
<img src='materials/juse_architecture_v1.png'>

클라이언트는 Netlify를 통해 배포하여 https 통신을 구현하였습니다. github repository와 연동하여 특정 브랜치에 push될 때 빌드하여 배포되도록 설정되었습니다.
</br>
</br>
백엔드 서버는 애플리케이션 서버와 DB, 이미지 서버 모두 여분의 데스크탑에서 온프레미스로 구성하였으며, 포트 포워딩을 통해 외부와 통신이 가능하도록 설정하였습니다.
</br>
</br>
HTTPS 통신을 하기 위해 무료 도메인 및 SSL 인증서를 제공하는 서비스를 이용하여 클라이언트 서버와 통신이 가능하도록 구성하였습니다.
</details>


### v2

v2부터는 클라우드 서비스로 서버 구성이 변경되었습니다. 

<img src='materials/juse_architecture_v2.png'>

클라이언트 서버는 빌드된 코드를 static web server 역할을 하는 버킷에 배포합니다. 이 버킷은 CloudFront 서비스를 통해 오리진으로 설정하였고, 
캐싱된 사본은 엣지인 CloudFront에서 관리하며 클라이언트로 요청이 들어왔을 때 이 캐시가 사용됩니다. CloudFront는 https 로 통신하도록 설정되었습니다.
</br>
</br>
백엔드 서버는 애플리케이션이 동작하고 있는 EC2 인스턴스 앞에 ALB를 두고 있으며, ALB는 https 통신을 하도록 설정되어있으므로 서버로 들어오는 요청을 필터링하는 프론트 프록시 역할을 하게 됩니다
(현재 버전에서는 인스턴스 내 애플리케이션은 하나만 설정되어 있어 리버스 프록시 기능은 하고 있지 않습니다).
</br>
</br>
EC2에서 실행되는 spring boot 애플리케이션과는 http 통신을 진행합니다. DB는 RDS MySQL을 사용하였습니다. 
</br>
</br>
Route53 서비스를 통해 들어온 요청을 설정된 주소에 맞게 라우팅합니다. 
클라이언트 서버로 들어온 요청은 CloudFront로, 백엔드 서버로 보내는 요청은 ALB로 각각 라우팅합니다. 
도메인을 AWS가 아닌 외부에서 구입하였으므로, 도메인 주소로 들어온 요청은 AWS 리소스로 라우팅 되도록 네임서버를 설정하였습니다.
</br>
</br>

### CI/CD
Github 레포지토리의 main 브랜치에 push 작업이 발생했을 경우 Github Actions을 통해 main 브랜치에 있는 소스 코드를 테스트, 압축 및 빌드하고, CodeDeploy 가 AWS 서비스에 압축 파일 및 빌드 결과물을 전달하도록 스크립트를 실행합니다. 
이 때 압축 파일은 버저닝을 위해 S3 버킷에 저장하고, 빌드된 파일은 EC2로 전달합니다. 
EC2에서 실행 중인 자바 프로세스를 확인하고, 있으면 종료 시킨 뒤 새로 전달받은 빌드를 실행시킵니다.
</br>
</br>

### 소셜 로그인
OAuth2에 기반하여 Github과 Google에 인증 작업을 위임합니다. 
EC2 인스턴스에서 실행되는 애플리케이션에서 작업을 요청하게 되며 이 때 채택된 방식은 Authorization Code Grant 입니다. 아래는 JUSE에서 소셜 로그인이 진행되는 흐름입니다.
</br>
</br>
<img src='materials/juse_oauth2_social_login.jpg'>

클라이언트 앱(리액트 앱)에서 유저 정보 동의를 얻으면 백엔드 앱(스프링 앱)으로 지정된 주소로 요청을 보냅니다. </br>
스프링에서는 구글, 깃헙의 인가 서버에 설정해놓은 리다이렉션 URI로 인가 코드를 요청하고, 이 코드를 통해 실제 유저 정보를 가지고 있는 구글, 깃헙의 리소스 서버에 접근할 수 있는 접근 토큰을 얻어옵니다. </br>
이 토큰을 통해 지정된 범위만큼의 정보(이메일, 프로필 사진 주소 등)를 리소스 서버에서 얻어옵니다. 정보를 얻어오는 데 성공하면 JWT를 발급하여 프론트로 전달합니다.
</br>
</br>

## 프로젝트 구조

MVC 패턴과 레이어드 아키텍처를 활용한 모놀리식 구조를 가지고 있습니다. 
</br>
</br>
모든 컴포넌트가 하나의 애플리케이션 내부에 포함되어 있으므로 코드 간의 종속성이 높지만, 유지보수와 테스트, 디버깅, 배포가 비교적 수월하다는 장점이 있습니다. 전통적인 웹 개발 방식이므로 레퍼런스가 풍부하고, 트래픽이 많지 않은 프로젝트에 도입하기 적합하였습니다.    
</br>
</br>

## DB 

<img src='materials/juse_database_scheme.png'>

외래키를 통해 N:1, N:M 등의 테이블의 관계를 표현하였고, 팀원과의 논의를 통해 정규화를 적용해보았습니다. 
</br>
</br>
프로젝트 초반 단계에서는 h2 와 hibernate 설정으로 스키마를 자동 생성되도록 하여 동작 여부를 확인하였습니다. 실제 개발 단계에서는 MySQL workbench를 통해 스키마를 작성한 뒤 로컬 서버를 통해 작업하였습니다.
</br>
</br>

### 트랜잭션 관리
Spring에서 지원하는 어노테이션 기반으로 트랜잭션을 관리하였습니다. MySQL 8.0의 기본엔진인 InnoDB의 디폴트 설정(repeatable read)를 사용하였습니다. 각 트랜잭션에 식별 번호를 부여하여 해당 트랜잭션이 작업하고 있는 레코드가 다른 트랜잭션에 영향을 미치지 않습니다.   

#### 동기 작업

서비스 레이어에서 트랜잭션들의 순서를 지정하고 하나의 트랜잭션으로 처리하기 위한 작업에 사용하였습니다. </br> 
`@Transactional`의 기본설정인 `Propagation.REQUIRED` 를 통해 해당 메서드에 포함되어 있는 작업들이 한 스레드로 묶여 하나의 트랜잭션으로 수행됩니다. (직접 설정한 객체 그래프가 아닌 JPA 프록시 객체를 사용하는 경우에도 설정해주었습니다) 
</br> 따라서 해당 메서드를 실행 중 예외가 발생하게 되면 커밋이 되지 않고 롤백이 됩니다. 

```java
public class BoardServiceImpl {
    
    private final UserService userService;
    private final TagService tagService;
    private final BoardReopsitory boardReopsitory;
    
    // constructor
    
    @Transactional
    public Board create(Board post){
        User user = userService.verifyByUser(post.getUser());
        Tag tag = tagService.verifyByTagName(post.getTagName());
        return boardReopsitory.save(post);
    }    
}

```

#### 비동기 작업 (이벤트 처리)
특정 작업 후 알림이 발행되는 이벤트의 경우 앞의 작업이 완료된 후 실행되어야 하고, 알림 발행 자체가 동기적으로 진행될 필요가 없으므로 트랜잭션이 커밋된 후 비동기 스레드로 응답하도록 설정하였습니다.

```java
@EnableAsync
@Component
public class NotificationEventListener {
    
    private final NotificationService notificationService;
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        Notification notification = event.getEvent();
        notificationService.send(notification);
    }
} 

```

## 예외 처리
알림기능
테스트



## 기획 문서

- [사용자 요구사항 정의서](https://docs.google.com/spreadsheets/d/1YQc8KwcKyAEsbhF_-LmGuVD35DMIdXNNGF-e1COYyHk/edit#gid=0)
- [화면 정의서](https://www.notion.so/67392033074049daaca51da6605c83af)

<br/>

## 💁🏻‍♀️ 팀원 소개

|                                                                                     방현수(BE 팀장)                                                                                      |                                                                               최영민(BE)                                                                                |                                                정우용(FE)                                                |                                               김은주(FE)                                               |
| :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------: |
| <img src="https://e7.pngegg.com/pngimages/91/840/png-clipart-super-mario-odyssey-super-mario-3d-land-new-super-mario-bros-wii-mario-heroes-nintendo-thumbnail.png" width=100 height=100> | <img src="https://static.wikia.nocookie.net/mariokart/images/0/09/GoombaNSMB.jpg/revision/latest/top-crop/width/360/height/450?cb=20080728222842" width=100 height=100> | <img src="https://i.pinimg.com/474x/78/58/5d/78585da7ee6b5cd6a58f35c9e39acc22.jpg" width=100 height=100> | <img src="https://upload.wikimedia.org/wikipedia/en/b/b2/Koopa_Troopa_NSMBU.png" width=100 height=100> |
|                                                                       [thom-droid](https://github.com/thom-droid)                                                                        |                                                                 [aprochoi](https://github.com/aprochoi)                                                                 |                                 [cleats01](https://github.com/cleats01)                                  |                                  [ekim49](https://github.com/ekim49)                                   |

<br/>

## 📚 기술 스택
#### Web Communication

![REST API](https://img.shields.io/badge/REST_API-005C84?style=flat) ![HTTPS](https://img.shields.io/badge/HTTPS-red?style=flat)
</br>

#### Frontend

![React](https://img.shields.io/badge/react-16-%2320232a.svg?style=flat)
![React Query](https://img.shields.io/badge/React%20Query-FF4154?style=flat)
![Styled Components](https://img.shields.io/badge/Styled--Components-DB7093?style=flat)
![JavaScript](https://img.shields.io/badge/Javascript-ES6-%23323330.svg?style=flat)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat)
</br>
#### Backend

![Java](https://img.shields.io/badge/Java-11-1572B6.svg?style=flat&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.3-6DB33F.svg?style=flat)
![Apache Tomcat](https://img.shields.io/badge/Apache_Tomcat-9.0-%23F8DC75.svg?style=flat)
![MySQL](https://img.shields.io/badge/MySQL-8.0-005C84?style=flat)
</br>
![AWS SDK](https://img.shields.io/badge/AWS_SDK-2.0-blue.svg?style=flat&logoColor=white)
![AWS](https://img.shields.io/badge/EC2-orange.svg?style=flat)
![AWS](https://img.shields.io/badge/CloudFront-blueviolet.svg?style=flat)
![AWS](https://img.shields.io/badge/ALB-yellow.svg?style=flat&logoColor=white)
![AWS](https://img.shields.io/badge/RDS-blue.svg?style=flat&logoColor=white)
![AWS](https://img.shields.io/badge/S3-darkeyellow.svg?style=flat&logoColor=white)
![AWS](https://img.shields.io/badge/CodeDeploy-blue.svg?style=flat&logoColor=white)
</br>

#### Tools & Collaboration

![Git](https://img.shields.io/badge/GIT-E44C30?style=flat&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-100000?style=flat&logo=github&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=Postman&logoColor=white)
![Netlify](https://img.shields.io/badge/netlify-%23000000.svg?style=flat&logo=netlify&logoColor=#00C7B7)
![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=flat&logo=figma&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=flat&logo=notion&logoColor=white)

