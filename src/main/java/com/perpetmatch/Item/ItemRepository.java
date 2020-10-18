package com.perpetmatch.Item;

import com.perpetmatch.Domain.Item.Item;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByCompany(String company);

    @Query("select distinct i from Item i")
    Slice<Item> findAllWithSlice(Pageable pageable);
}
