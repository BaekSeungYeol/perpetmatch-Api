package com.perpetmatch.initDb;


import com.perpetmatch.Domain.*;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.Role.RoleRepository;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor

public class InitDB {
//    private final InitService initService;
//
//    @PostConstruct
//    public void init() throws IOException {
//        initService.initRoleData();
//        initService.initZoneData();
//        initService.initageRangeData();
//        initService.petTitleData();
//    }
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService {
//
//        private final RoleRepository roleRepository;
//        private final ZoneRepository zoneRepository;
//        private final PetAgeRepository petAgeRepository;
//        private final PetRepository petRepository;
//
//        public void initRoleData() {
//            if (roleRepository.count() == 0) {
//                Role role = new Role(RoleName.ROLE_USER);
//                roleRepository.save(role);
//                Role role2 = new Role(RoleName.ROLE_ADMIN);
//                roleRepository.save(role2);
//            }
//        }
//
//        public void initageRangeData() {
//            if(petAgeRepository.count() == 0) {
//                PetAge petAge = PetAge.builder().petRange("1년이하")
//                        .build();
//                petAgeRepository.save(petAge);
//                PetAge petAge2 = PetAge.builder().petRange("1년~7년")
//                        .build();
//                petAgeRepository.save(petAge2);
//                PetAge petAge3 = PetAge.builder().petRange("7년이상")
//                        .build();
//                petAgeRepository.save(petAge3);
//
//            }
//        }
//        public void petTitleData() throws IOException {
//            if(petRepository.count() == 0) {
//                Resource resource = new ClassPathResource("petTitle.csv");
//                List<Pet> collect = Files.readAllLines(resource.getFile().toPath(),StandardCharsets.UTF_8).stream()
//                        .map(line -> {
//                            String[] lines = line.split(",");
//                            return Pet.builder().title(lines[0]).build();
//                        }).collect(Collectors.toList());
//
//                petRepository.saveAll(collect);
//            }
//        }
//
//
//        public void initZoneData() throws IOException {
//            if(zoneRepository.count() == 0) {
//                Resource resource = new ClassPathResource("zones_kr.csv");
//                List<Zone> collect = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
//                        .map(line -> {
//                            String[] lines = line.split(",");
//                            return Zone.builder().province(lines[0]).build();
//                        }).collect(Collectors.toList());
//
//                zoneRepository.saveAll(collect);
//            }
//        }
//    }
}
