package com.perpetmatch.modules.pet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

//    public Pet findOrCreateNew(String kind) {
//        Pet pet = petRepository.findByKind(kind);
//        if(pet == null) {
//            pet = petRepository.save(Pet.builder().kind(kind).build());
//        }
//    }
}
