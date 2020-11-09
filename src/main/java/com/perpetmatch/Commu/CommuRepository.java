package com.perpetmatch.Commu;

import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.Commu;
import com.perpetmatch.Domain.Delivery;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommuRepository extends JpaRepository<Commu, Long> {

    @Query("select distinct c from Commu c left join fetch c.comments order by c.createdAt")
    List<Commu> findAllWithComment();

}
