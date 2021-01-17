package com.perpetmatch.AdoptBoard.domain;

import com.perpetmatch.User.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long>, BoardRepositoryCustom{


   // @Query("select distinct b from Board b left join fetch b.petAge left join fetch b.zone left join fetch b.petTitle where b.id = :id")
    @EntityGraph(attributePaths = {"petAge","zone","petTitle", "manager"})
    Board findZoneAndPetTitleAndPetAgeById(@Param("id") Long id);


    Board findByTitle(String s);

    @Query("select distinct b from Board b left join fetch b.petAge left join fetch b.zone left join fetch b.petTitle")
    Slice<Board> findAllBoards(Pageable pageable);

   @Query("select distinct b from Board b left join fetch b.petAge left join fetch b.zone left join fetch b.petTitle")
   List<Board> findAllWith();

    @Query("select b from Board b left join fetch b.users where b.id = :id")
    Board findByIdWithUsers(@Param("id") Long id);

    @Query("select distinct b from Board b left join fetch b.petAge left join fetch b.zone left join fetch b.petTitle left join fetch b.manager where b.manager = :user")
    List<Board> findWithManager(@Param("user") User user);

}
