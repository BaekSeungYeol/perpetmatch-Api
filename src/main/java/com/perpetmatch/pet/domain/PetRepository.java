package com.perpetmatch.pet.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet,Long> {
    Pet findByTitle(String kind);
}
