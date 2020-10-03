package com.perpetmatch.initDb;


import com.perpetmatch.Domain.*;
import com.perpetmatch.Domain.Item.Feed;
import com.perpetmatch.Domain.Item.Goods;
import com.perpetmatch.Domain.Item.Snack;
import com.perpetmatch.Item.ItemRepository;
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
        initService.putItems();
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
        private final ItemRepository itemRepository;


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

        public void putItems() {
            if(itemRepository.count() == 0) {
                Feed feed1 = new Feed();
                feed1.setName("네츄럴 코어 유기농 비프 애견사료");
                feed1.setPrice(51990);
                feed1.setStockQuantity(100);
                itemRepository.save(feed1);

                Feed feed2 = new Feed();
                feed2.setName("하림 더리얼 치킨 애견사료");
                feed2.setPrice(45990);
                feed2.setStockQuantity(100);
                itemRepository.save(feed2);

                Feed feed3 = new Feed();
                feed3.setName("뉴웨이브 유기농 애견사료");
                feed3.setPrice(31990);
                feed3.setStockQuantity(100);
                itemRepository.save(feed3);

                Snack snack1 = new Snack();
                snack1.setName("터키츄 칠면조힘줄 스트립");
                snack1.setPrice(8500);
                snack1.setStockQuantity(100);
                itemRepository.save(snack1);

                Snack snack2 = new Snack();
                snack2.setName("퓨리나 프로플랜 강아지 덴탈껌");
                snack2.setPrice(10500);
                snack2.setStockQuantity(100);
                itemRepository.save(snack2);

                Snack snack3 = new Snack();
                snack3.setName("수제간식 질겅질겅 소떡심");
                snack3.setPrice(4500);
                snack3.setStockQuantity(100);
                itemRepository.save(snack3);


                Goods goods1 = new Goods();
                goods1.setName("플라밍고 우디 스타 나무모양의 고무 장난감");
                goods1.setPrice(8800);
                goods1.setStockQuantity(100);
                itemRepository.save(goods1);


                Goods goods2 = new Goods();
                goods2.setName("폭탄 간식놀이 노즈워크 장난감");
                goods2.setPrice(4500);
                goods2.setStockQuantity(100);
                itemRepository.save(goods2);

                Goods goods3 = new Goods();
                goods3.setName("플레넷도그 오르비 터프볼");
                goods3.setPrice(25000);
                goods3.setStockQuantity(100);
                itemRepository.save(goods3);
            }
        }
    }
}
