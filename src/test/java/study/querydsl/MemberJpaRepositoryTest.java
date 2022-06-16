package study.querydsl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberJpaRepository;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private EntityManager em;

    @Test
    public void basicTest() {
        Member member1 = new Member("member1", 10);
        memberJpaRepository.save(member1);

        Member member = memberJpaRepository.findById(member1.getId()).get();
        assertThat(member).isEqualTo(member1);

        List<Member> result1 = memberJpaRepository.findByUsername("member1");
        assertThat(result1).containsExactly(member1);

        List<Member> result2 = memberJpaRepository.findAll();
        assertThat(result2).containsExactly(member1);

        List<Member> result3 = memberJpaRepository.findByUsername_querydsl("member1");
        assertThat(result3).containsExactly(member1);

        List<Member> result4 = memberJpaRepository.findAll_querydsl();
        assertThat(result4).containsExactly(member1);
    }

    @Test
    public void builderSearchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeLoe(40);
        condition.setAgeGoe(35);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("member4");
    }
}
