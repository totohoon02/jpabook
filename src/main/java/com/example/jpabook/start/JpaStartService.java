package com.example.jpabook.start;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
