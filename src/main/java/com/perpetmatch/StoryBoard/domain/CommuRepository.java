package com.perpetmatch.StoryBoard.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommuRepository extends JpaRepository<Commu, Long> {

    @Query("select distinct c from Commu c left join fetch c.comments order by c.createdAt desc")
    List<Commu> findAllWithComment();

}
