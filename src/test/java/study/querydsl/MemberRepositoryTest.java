package study.querydsl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager em;

    @Test
    public void basicTest() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        Member member = memberRepository.findById(member1.getId()).get();
        assertThat(member).isEqualTo(member1);

        List<Member> result1 = memberRepository.findByUsername("member1");
        assertThat(result1).containsExactly(member1);

        List<Member> result2 = memberRepository.findAll();
        assertThat(result2).containsExactly(member1);

        List<Member> result3 = memberRepository.findByUsername("member1");
        assertThat(result3).containsExactly(member1);

        List<Member> result4 = memberRepository.findAll();
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

        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    public void searchComplexTest() {
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
//        condition.setAgeLoe(40);
//        condition.setAgeGoe(35);
//        condition.setTeamName("teamB");

        PageRequest pageable = PageRequest.of(0, 2);

        Page<MemberTeamDto> result = memberRepository.searchPageComplex(condition, pageable);


//        assertThat(result.getSize()).isEqualTo(3);
//        assertThat(result.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
        System.out.println("result.getNumber() = " + result.getNumber());
        System.out.println("result.getTotalPages() = " + result.getTotalPages());
        System.out.println("result.getTotalElements() = " + result.getTotalElements());
        System.out.println("result.getContent() = " + result.getContent());
        for (MemberTeamDto memberTeamDto : result.getContent()) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }



}
