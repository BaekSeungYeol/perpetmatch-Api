package com.perpetmatch.initDb;

import com.perpetmatch.Domain.Role;
import com.perpetmatch.Domain.RoleName;
import com.perpetmatch.Role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
@RequiredArgsConstructor

public class InitDB {
//    private final InitService initService;
//
//    @PostConstruct
//    public void init() {
//        initService.dbInit1();
//    }
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService {
//
//        private final RoleRepository roleRepository;
//
//        public void dbInit1() {
//            Role role = new Role(RoleName.ROLE_USER);
//            roleRepository.save(role);
//            Role role2 = new Role(RoleName.ROLE_ADMIN);
//            roleRepository.save(role2);
//        }
//    }
}
