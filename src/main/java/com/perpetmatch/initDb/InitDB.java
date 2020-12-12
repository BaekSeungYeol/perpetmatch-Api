package com.perpetmatch.initDb;


import com.perpetmatch.Domain.*;
import com.perpetmatch.Domain.Item.*;
import com.perpetmatch.modules.Item.ItemRepository;
import com.perpetmatch.modules.PetAge.PetAgeRepository;
import com.perpetmatch.modules.Role.RoleRepository;
import com.perpetmatch.modules.Zone.ZoneRepository;
import com.perpetmatch.modules.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
                feed1.setTitle("벅스펫 국내산 유기농 베지믹스 관절건강 사료");
                feed1.setPrice(51990);
                feed1.setStockQuantity(100);
                feed1.setCompany("벨리스");
                feed1.setSale(20);
                feed1.setBoardImageHead("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%B2%85%EC%8A%A41.jpg");
                feed1.setBoardImageMain("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%B2%85%EC%8A%A4d+1.jpg");
                String dateString = "2020-10-01T10:11:30";
                feed1.setPublishedDateTime(LocalDateTime.parse(dateString));
                feed1.getOptions().add("유기농");
                feed1.getOptions().add("일반");

                itemRepository.save(feed1);

                Feed feed2 = new Feed();
                feed2.setTitle("인스팅트 RBK 피부건강 독키블 사료");
                feed2.setPrice(45990);
                feed2.setStockQuantity(100);
                feed2.setCompany("벨리스");
                feed2.setSale(15);
                feed2.setBoardImageHead("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%B2%85%EC%8A%A42.jpg");
                feed2.setBoardImageMain("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%B2%85%EC%8A%A42+d.jpg");
                dateString = "2020-10-02T10:11:30";
                feed2.setPublishedDateTime(LocalDateTime.parse(dateString));
                feed2.getOptions().add("유기농");
                feed2.getOptions().add("일반");

                itemRepository.save(feed2);

                Feed feed3 = new Feed();
                feed3.setTitle("인스팅트 독 생식본능 독키블");
                feed3.setPrice(31990);
                feed3.setStockQuantity(100);
                feed3.setCompany("벨리스");
                feed3.setSale(20);
                feed3.setBoardImageHead("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%B2%85%EC%8A%A43.jpg");
                feed3.setBoardImageMain("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%B2%85%EC%8A%A43d.jpg");
                dateString = "2020-10-04T10:11:30";
                feed3.setPublishedDateTime(LocalDateTime.parse(dateString));
                feed3.getOptions().add("유기농");
                feed3.getOptions().add("일반");
                itemRepository.save(feed3);

                Snack snack1 = new Snack();
                snack1.setTitle("밸리스 만능 츄르");
                snack1.setPrice(8500);
                snack1.setStockQuantity(100);
                snack1.setCompany("마이비펫");
                snack1.setSale(10);
                snack1.setBoardImageHead("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EC%9D%B4%EB%AF%B8%EC%A7%80+42%402x.png");
                snack1.setBoardImageMain("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%A7%8C%EB%8A%A5d.jpg");
                dateString = "2020-10-03T10:11:30";
                snack1.setPublishedDateTime(LocalDateTime.parse(dateString));
                snack1.getOptions().add("순한맛");
                snack1.getOptions().add("보통맛");
                itemRepository.save(snack1);

                Snack snack2 = new Snack();
                snack2.setTitle("밸리스 에취 츄르");
                snack2.setPrice(10500);
                snack2.setStockQuantity(100);
                snack2.setCompany("마이비펫");
                snack2.setSale(10);
                snack2.setBoardImageHead("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EC%97%90%EC%B8%84.jpg");
                snack2.setBoardImageMain("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EC%97%90%EC%B8%84d.jpg");
                dateString = "2020-10-05T10:11:30";
                snack2.setPublishedDateTime(LocalDateTime.parse(dateString));
                snack2.getOptions().add("순한맛");
                snack2.getOptions().add("보통맛");
                itemRepository.save(snack2);

                Snack snack3 = new Snack();
                snack3.setTitle("밸리스 꿀잠 츄르");
                snack3.setPrice(4500);
                snack3.setStockQuantity(100);
                snack3.setCompany("마이비펫");
                snack3.setSale(10);
                snack3.setBoardImageHead("https://shopbucket.s3.ap-northeast-2.amazonaws.com/2949540189_B.jpg");
                snack3.setBoardImageMain("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EA%BF%80%EC%9E%A0d.jpg");
                dateString = "2020-10-06T10:11:30";
                snack3.setPublishedDateTime(LocalDateTime.parse(dateString));
                snack3.getOptions().add("순한맛");
                snack3.getOptions().add("보통맛");
                itemRepository.save(snack3);


                Goods goods1 = new Goods();
                goods1.setTitle("강아지 노즈워크 스니프 볼");
                goods1.setPrice(27000);
                goods1.setStockQuantity(100);
                goods1.setCompany("까르페띠앙");
                goods1.setSale(15);
                goods1.setBoardImageHead("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%8C%95%EB%83%A5.jpg");
                goods1.setBoardImageMain("https://shopbucket.s3.ap-northeast-2.amazonaws.com/%EB%B0%A9%EC%84%9Dd.png");
                dateString = "2020-10-07T10:11:30";
                goods1.setPublishedDateTime(LocalDateTime.parse(dateString));
                goods1.getOptions().add("회색");
                goods1.getOptions().add("파랑색");
                goods1.getOptions().add("검은색");
                Goods savedGoods1 = itemRepository.save(goods1);


            }
        }
    }
}
