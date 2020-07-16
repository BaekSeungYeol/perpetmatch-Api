package com.perpetmatch.Member;

import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.Member;
import com.perpetmatch.Domain.Role;
import com.perpetmatch.Domain.RoleName;
import com.perpetmatch.Role.RoleRepository;
import com.perpetmatch.apiDto.JoinMemberRequest;
import com.perpetmatch.apiDto.UpdateMemberRequest;
import com.perpetmatch.exception.AppException;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public void sendJoinMemberConfirmEmail(Member savedMember) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedMember.getEmail());
        mailMessage.setSubject("퍼펫매치, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + savedMember.getEmailCheckToken() +
                "&email=" + savedMember.getEmail());
        //인터페이스만 관리
        // TODO SMTP 서버 연결해서 GMAIL로 보내보기
        javaMailSender.send(mailMessage);
    }

    public Member join(SignUpRequest request) {

        Member member = Member.builder().nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();


        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        member.setRoles(Collections.singleton(userRole));

        Member savedMember = memberRepository.save(member);

        savedMember.generateEmailCheckToken();
        sendJoinMemberConfirmEmail(savedMember);

        return savedMember;
    }

    public void update(Long id, UpdateMemberRequest request) {

        Optional<Member> byId = memberRepository.findById(id);
        Member Member = byId.get();

        Member = Member.builder()
                .nickname(request.getName())
                .password(request.getPassword()).build();
    }


    private boolean validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findAllByNickname(member.getNickname());
        if(!findMembers.isEmpty()) {
            return true;
        }
        return false;
    }

    public Member findOne(Long id) {
        Optional<Member> byId = memberRepository.findById(id);
        return byId.get();
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }


    public void addPet(String managerName, Pet pet) {
        Member Member = memberRepository.findByNickname(managerName);
        Member.getPet().add(pet);
    }


    public boolean verifyingEmail(String token, String email) {

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return false;
        }

        if (!member.getEmailCheckToken().equals(token)) {
            return false;
        }
        member.completeSignup(token);
        login(member);
        return true;
    }

    public void login(Member member) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserMember(member),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);

//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                username,password);
//        Authentication authentication = authenticationManager.authenticate(token);
//        context.setAuthentication(authentication);
    }
}
