# 자바 ORM 표준 프로그래밍

## JPA

### 1.1 SQL을 직접 다룰 때 발생하는 문제점

- 수 많은 반복적인 코드와 SQL 작성
- SQL에 의존적인 개발
  - 요구사항의 변경 시 수 많은 수정사항
- 연관된 객체
  - 추가 SQL이 필요하다

#### JPA와 문제 해결

- 직접 SQL을 작성하지 않고 JPA가 제공하는 API 사용
- JPA가 클래스의 매핑 정보를 보고 적절한 SQL을 생성한다.

### 1.2 패러다임 불일치

- 복잡성을 제어하기 위한 `객체지향 프로그래밍`
- 데이터를 저장하기 위해서는 데이터베이스를 사용, 데이터 베이스에는 객체지향의 개념이 없다

#### 상속

- DB의 상속

  - 슈퍼타입과 서브타입
  - 테이블을 나누어 저장함

    ```sql
    INSERT INTO ITEM ...
    INSERT INTO ALBUM ...
    ```

  - 조회 시에도 테이블 조인을 통해 객체를 생성

- JPA의 상속
  - JPA가 알아서 SQL을 생성

#### 연관관계

- DB: id로 참조
- Java: 객체를 사용한 참조
- 중간에 변환 JPA

### 1.3 JPA란?

> Java Persistence API

- 자바 진영 ORM 표준
- 어플리케이션과 JDBC API 사이에서 동작
  - 내부적으로 JDBC 기술을 사용
- JPA 자체는 인터페이스
  - 주로 하이버네이트 구현체가 사용된다.

## JPA 시작

> Data JPA와 MySQL 사용

- DataJPA를 사용하면 `Persistence.xml` 설정 불필요
- JDBC ~ JPA 모두 포함

<details>
<summary>Requirements</summary>
<div markdown="1">

`build.gradle`

```yml
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'com.mysql:mysql-connector-j'
```

`application.yml`

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    # for local test allowPublicKeyRetrieval=true
    url: jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: root
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect # or org.hibernate.dialect.MySQLDialect for older versions
    hibernate:
      ddl-auto: update
    show-sql: false
```

`docker-compose.yml`

```yml
services:
  mysql:
    image: mysql:latest
    container_name: mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: test
      MYSQL_USER: mysql
      MYSQL_PASSWORD: mysql
    ports:
      - "3306:3306"
    volumes:
      - ./mysql:/val/lib/mysql
```

</div>
</details>

### 2.4 객체 매핑 시작

```sql
CREATE TABLE MEMBER (
    ID VARCHAR(255) NOT NULL,
    NAME VARCHAR(255),
    AGE INTEGER,
    PRIMARY KEY (ID)
)
```

- 테이블 생성

`Member.java`

```java
@Getter
@Setter
@Entity
@Table(name = "MEMBER")
public class Member {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "NAME")
    private String username;
    private Integer age;
}
```

`JpaStartServiceTest.java`

```java
@SpringBootTest
public class JpaServiceTest {

    @Autowired
    private JpaStartService jpaStartService;

    @Test
    void startTxTest() {
        jpaStartService.startTransaction();
    }
}
```

`JpaStartService.java`

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class JpaStartService {
    private final EntityManagerFactory emf;

    public void startTransaction() {
        // 엔티티매니저 팩토리는 스프링에 의해 제공됨
        // EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        EntityManager em = emf.createEntityManager();

        // Get Transaction
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();         // 트랜잭션 시작
            logic(em);          // 비지니스 로직 실행
            tx.commit();        // 트랜잭션 커밋

        } catch (Exception e) {
            tx.rollback();      // 예외 발생 시 롤백
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void logic(EntityManager em) {
        String id = "id2";
        Member member = new Member();
        member.setId(id);
        member.setUsername("이름");
        member.setAge(1221);

        // 등록
        em.persist(member);

        // 수정
        member.setAge(20);

        // 조회
        Member member1 = em.find(Member.class, id);
        log.info("find member: {}", member1);

        // 삭제
        em.remove(member);
    }
}
```

- `emf`
  - 어플리케이션에서 한번만 생성, 공유(스프링 컨테이너에서 관리)
  - 구현체에 따라 커넥션 풀도 생성, 비용이 큼
- `em`
  - 커넥션과 밀접한 관계
  - 스레드간 공유하면 안됨
- `CRUD`

## 영속성 관리

### 3.2 영속성 컨텍스트

```java
em.persist(member);
```

- 엔티티를 영구 저장하는 환경
- 엔티티 매니저로 엔티티를 다루면 엔티티 매니저는 영속성 컨텍스트에 엔티티를 보관, 관리

### 3.3 생명주기

- 비영속
  - 순수한 자바 객체
- 영속
  - 객체가 영속화되어 엔티티 매니저에 의해 관리
- 준영속
  - 컨텍스트에서 분리, 관리되지 않는 상태
- 삭제

### 3.4 특징

- 엔티티는 식별자(`@Id`)로 구분
- 영속화 -> 커밋 -> 플러시 -> DB에 저장
- 1차 캐시
  - 영속성 컨텍스트 내부의 캐시
  - `em.find(Member.class, "member1")`
  - 1차 캐시에서 우선적으로 조회, 없을 시 DB 조회
  - 성능 상 이점
- 동일성 보장
  - 동일한 객체
- 쓰기지연
  - 영속성 컨텍스트에 쿼리를 쌓아둔다.
  - `commit`이 발생하기 전까지 쓰지 않음
  - `@DynamicUpdate`를 이용해 일부 업데이트도 가능(>30)
- 변경 감지
  - 더티 체킹
  - JPA는 영속화 시에 `스냅샷`(엔티티의 최초 상태)을 생성, 플러시 시점에 비교해서 변경점을 반영
  - 모든 필드를 업데이트함
- 지연 로딩
  - 연관된 엔티티를 바로 조회하지 않고 필요한 시점에 쿼리

### 3.5 플러시

- 컨텍스트의 내용을 DB에 반영
- 더티 체킹 -> 쿼리 전송
- JPA는 커밋 시에 플러시를 자동으로 호출한다.

## 엔티티 매핑

```java
@Column(name = "username", nullable=false)
private String username;
```

## 연관관계 매핑

- 객채의 `참조`와 테이블의 `외래 키`를 매핑
- 방향
- 다중성
- 연관관계의 주인
