package com.perpetmatch.Member;

import com.perpetmatch.Domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByNickname(String name);

    List<User> findAllByNickname(String name);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    User findByEmail(String s);

    Optional<User> findByNicknameOrEmail(String nickname, String email);

    @Query("select distinct u from User u left join fetch u.petAges left join fetch u.petTitles left join fetch u.petAges where u.id = :id")
    Optional<User> findByIdWithTags(@Param("id") Long id);

    @Query("select distinct u from User u left join fetch u.bag where u.id = :id")
    User findByIdWithBags(@Param("id") Long id);
}
