package com.perpetmatch.pet;

import com.perpetmatch.Domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet,Long> {
    Pet findByTitle(String kind);
}
