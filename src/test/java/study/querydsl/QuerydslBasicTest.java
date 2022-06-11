package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@Transactional
@SpringBootTest
public class QuerydslBasicTest {

    @Autowired
    private EntityManager em;

    private JPAQueryFactory jpaQueryFactory;

    @BeforeEach
    public void before() {
        jpaQueryFactory = new JPAQueryFactory(em);
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
    }

    @Test
    public void startJPQL() {
        //member1을 찾아라.
        String qlString =
                "select m from Member m " +
                        "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {

        Member member1 = jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(member1.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member member1 = jpaQueryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.between(5, 15)))
                .fetchOne();

        assertThat(member1.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        Member member1 = jpaQueryFactory
                .selectFrom(member)
                .where(member.username.contains("er1"),
                        member.age.ne(15))
                .fetchOne();

        assertThat(member1.getUsername()).isEqualTo("member1");
    }
    
    @Test
    public void results() {
        //List
        List<Member> fetch = jpaQueryFactory
                .selectFrom(member)
                .fetch();
        //단 건
        Member findMember1 = jpaQueryFactory
                .selectFrom(member)
                .fetchOne();
        //처음 한 건 조회
        Member findMember2 = jpaQueryFactory
                .selectFrom(member)
                .fetchFirst();


        //페이징에서 사용(복잡한 쿼리에서 제대로 count를 못 구하는 문제때문에 미지원 전환)
//        QueryResults<Member> results = jpaQueryFactory
//                .selectFrom(member)
//                .fetchResults();
//        //count 쿼리로 변경
//        long count = jpaQueryFactory
//                .selectFrom(member)
//                .fetchCount();

        // 카운트 쿼리는 아래와같이 사용
        Long count = jpaQueryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        Long countAll = jpaQueryFactory
                .select(Wildcard.count)
                .from(member)
                .fetchOne();

        System.out.println("count = " + count);
        System.out.println("countAll = " + countAll);
//        assertThat(count).isEqualTo(4);
//        assertThat(countAll).isEqualTo(4);
    }
}
