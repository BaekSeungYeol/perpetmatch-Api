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
import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
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


        private String company;
        public void putItems() {
            if(itemRepository.count() == 0) {
                Feed feed1 = new Feed();
                feed1.setTitle("인섹트도그 미니 저알러지사료");
                feed1.setPrice(51990);
                feed1.setStockQuantity(100);
                feed1.setCompany("벨리스");
                feed1.setSale(20);
                feed1.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/11.jpg");
                itemRepository.save(feed1);

                Feed feed2 = new Feed();
                feed2.setTitle("인섹트도그 베기도그 다이어트사료");
                feed2.setPrice(45990);
                feed2.setStockQuantity(100);
                feed2.setCompany("벨리스");
                feed2.setSale(15);
                feed2.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/22.jpg");
                itemRepository.save(feed2);

                Feed feed3 = new Feed();
                feed3.setTitle("벨포아 홀리스틱 곤충사료 강아지 눈물사료");
                feed3.setPrice(31990);
                feed3.setStockQuantity(100);
                feed3.setCompany("벨리스");
                feed3.setSale(20);
                feed3.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/33.jpg");
                itemRepository.save(feed3);

                Snack snack1 = new Snack();
                snack1.setTitle("밸리스 만능 츄르");
                snack1.setPrice(8500);
                snack1.setStockQuantity(100);
                snack1.setCompany("마이비펫");
                snack1.setSale(10);
                snack1.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EC%9D%B4%EB%AF%B8%EC%A7%80+42%402x.png");
                itemRepository.save(snack1);

                Snack snack2 = new Snack();
                snack2.setTitle("밸리스 날씬 츄르");
                snack2.setPrice(10500);
                snack2.setStockQuantity(100);
                snack2.setCompany("마이비펫");
                snack2.setSale(10);
                snack2.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/2691714979_A1(1).jpg");
                itemRepository.save(snack2);

                Snack snack3 = new Snack();
                snack3.setTitle("밸리스 꿀잠 츄르");
                snack3.setPrice(4500);
                snack3.setStockQuantity(100);
                snack3.setCompany("마이비펫");
                snack3.setSale(10);
                snack3.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/2949540189_B.jpg");
                itemRepository.save(snack3);


                Goods goods1 = new Goods();
                goods1.setTitle("강아지 노즈워크 스니프 볼");
                goods1.setPrice(27000);
                goods1.setStockQuantity(100);
                goods1.setCompany("까르페띠앙");
                goods1.setSale(15);
                goods1.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EC%9D%B4%EB%AF%B8%EC%A7%80+40%403x.png");
                itemRepository.save(goods1);


                Goods goods2 = new Goods();
                goods2.setTitle("노즈워크 종합 장난감");
                goods2.setPrice(4500);
                goods2.setStockQuantity(100);
                goods2.setCompany("까르페띠앙");
                goods2.setSale(15);
                goods2.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/215025063170%403x.png");
                itemRepository.save(goods2);

                Goods goods3 = new Goods();
                goods3.setTitle("플레넷도그 오르비 터프 방석");
                goods3.setPrice(25000);
                goods3.setStockQuantity(100);
                goods3.setCompany("까르페띠앙");
                goods3.setSale(15);
                goods3.setImage("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EC%9D%B4%EB%AF%B8%EC%A7%80+41%403x.png");
                itemRepository.save(goods3);
            }
        }
    }
}
