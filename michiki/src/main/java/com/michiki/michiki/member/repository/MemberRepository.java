package com.michiki.michiki.member.repository;

import com.michiki.michiki.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

// Member 엔티티에  대한 JPA Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 소셜 ID를 이용해 유저 조회
    Optional<Member> findBySocialId(String socialId);

    // 이메일을 이용해 유저 조회
    Optional<Member> findByEmail(String email);
}
