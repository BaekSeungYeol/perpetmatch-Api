package com.perpetmatch.Board;

import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.PetAge;
import com.perpetmatch.Domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

}
