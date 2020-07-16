package com.perpetmatch.pet;

import com.perpetmatch.Domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet,Long> {
    Pet findByKind(String kind);
}
