package com.perpetmatch.Item.domain;

import com.perpetmatch.pet.domain.PetAge;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByCompany(String company);

    @Query("select distinct i from Item i")
    Slice<Item> findAllWithSlice(Pageable pageable);

    @Query("select distinct i from Item i left join fetch i.options where i.title = :title")
    Item findByTitleWithOpt(@Param("title") String title);

    @Query("select p from PetAge p where p.petRange = :petRange")
    PetAge findPetRange(@Param("petRange") String petRange);
}
