package com.perpetmatch.Member;

import com.perpetmatch.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByNickname(String name);

    List<Member> findAllByNickname(String name);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Member findByEmail(String s);

    Optional<Member> findByNicknameOrEmail(String nickname, String email);
}
