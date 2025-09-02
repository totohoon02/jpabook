package com.example.jpabook.start;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class RelationTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    void 팀_저장_테스트() {
        Team team = new Team("team1", "팀1");
        em.persist(team);

        // member 1
        Member member1 = new Member("m1", "멤버1", 20, team);
        em.persist(member1);

        assertThat(member1.getTeam()).isEqualTo(team);
    }
}
