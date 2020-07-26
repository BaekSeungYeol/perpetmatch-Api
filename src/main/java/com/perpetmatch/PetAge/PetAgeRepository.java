package com.perpetmatch.PetAge;

import com.perpetmatch.Domain.PetAge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetAgeRepository extends JpaRepository<PetAge,Long> {


    @Query("select p from PetAge p where p.petRange = :petRange")
    PetAge findPetRange(@Param("petRange") String petRange);

    @Query("select p.petRange from PetAge p")
    List<String> findAllRanges();

}
