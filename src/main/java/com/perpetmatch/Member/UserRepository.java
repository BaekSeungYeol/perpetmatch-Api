package com.perpetmatch.Member;

import com.perpetmatch.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByNickname(String name);

    List<User> findAllByNickname(String name);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    User findByEmail(String s);

    Optional<User> findByNicknameOrEmail(String nickname, String email);
}
