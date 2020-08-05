package com.perpetmatch.initDb;


import com.perpetmatch.Domain.*;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.Role.RoleRepository;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.internal.util.BytesHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor

public class InitDB {
    private final InitService initService;

    @PostConstruct
    public void init() throws IOException {
        initService.initRoleData();
        initService.initZoneData();
        initService.initageRangeData();
        initService.petTitleData();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final ResourceLoader resourceLoader;
        private final RoleRepository roleRepository;
        private final ZoneRepository zoneRepository;
        private final PetAgeRepository petAgeRepository;
        private final PetRepository petRepository;

        public void initRoleData() {
            if (roleRepository.count() == 0) {
                Role role = new Role(RoleName.ROLE_USER);
                roleRepository.save(role);
                Role role2 = new Role(RoleName.ROLE_ADMIN);
                roleRepository.save(role2);
            }
        }

        public void initageRangeData() {
            if (petAgeRepository.count() == 0) {
                PetAge petAge = PetAge.builder().petRange("1년이하")
                        .build();
                petAgeRepository.save(petAge);
                PetAge petAge2 = PetAge.builder().petRange("1년~7년")
                        .build();
                petAgeRepository.save(petAge2);
                PetAge petAge3 = PetAge.builder().petRange("7년이상")
                        .build();
                petAgeRepository.save(petAge3);

            }
        }

        public void petTitleData() throws IOException {
            if (petRepository.count() == 0) {
                ClassPathResource resource = new ClassPathResource("petTitle.csv");

                byte[] data = FileCopyUtils.copyToByteArray(resource.getInputStream());
                String val = new String(data, StandardCharsets.UTF_8);
                List<String> datas = Arrays.asList(val.split("[\\r\\n]+"));

                List<Pet> collect = datas.stream().map(line -> {
                    return Pet.builder().title(line).build();
                }).collect(Collectors.toList());

                petRepository.saveAll(collect);
//                List<Pet> collect = Files.readAllLines(resource1.getFile().toPath(), StandardCharsets.UTF_8).stream()
//                        .map(line -> {
//                            String[] lines = line.split(",");
//                            return Pet.builder().title(lines[0]).build();
//                        }).collect(Collectors.toList());
//
//                petRepository.saveAll(collect);
            }
        }


        public void initZoneData() throws IOException {
            if (zoneRepository.count() == 0) {
                ClassPathResource resource = new ClassPathResource("zonesList.csv");

                byte[] data = FileCopyUtils.copyToByteArray(resource.getInputStream());
                String val = new String(data, StandardCharsets.UTF_8);
                List<String> datas = Arrays.asList(val.split("[\\r\\n]+"));

                List<Zone> collect = datas.stream().map(p -> {
                    return Zone.builder().province(p).build();
                }).collect(Collectors.toList());

                zoneRepository.saveAll(collect);

//                Resource resource = new ClassPathResource("zonesList.csv");
//                List<Zone> collect = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
//                        .map(line -> {
//                            String[] lines = line.split(",");
//                            return Zone.builder().province(lines[0]).build();
//                        }).collect(Collectors.toList());
//
//                zoneRepository.saveAll(collect);
            }
        }
        private static List<Byte> convertBytesToList(byte[] bytes) {
            final List<Byte> list = new ArrayList<>();
            for (byte b : bytes) {
                list.add(b);
            }
            return list;
        }
    }
}
