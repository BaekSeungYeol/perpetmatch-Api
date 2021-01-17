package com.perpetmatch.pet.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PetAgeService {

    private final PetAgeRepository petAgeRepository;
}
