package com.example.jpabook.start;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class PersistenceContextTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    void 영속성컨텍스트_동일성_검증() {
        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("hoon");
        member1.setAge(20);

        em.persist(member1);

        Member find1 = em.find(Member.class, "member1");
        Member find2 = em.find(Member.class, "member1");

        // 1차 캐시 동일성
        assertThat(find1).isSameAs(find2);
        assertThat(em.contains(member1)).isTrue();
    }

    @Test
    void 변경감지_및_flush_검증() {
        Member member = new Member();
        member.setId("m1");
        member.setUsername("hoon");
        member.setAge(20);
        em.persist(member);

        member.setAge(22); // 더티 체킹
        em.flush();        // 즉시 SQL 반영
        em.clear();        // 1차 캐시 비우기

        Member reloaded = em.find(Member.class, "m1");
        assertThat(reloaded.getAge()).isEqualTo(22);
        assertThat(reloaded).isNotSameAs(member);
    }

}
