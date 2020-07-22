package com.perpetmatch.Member;

import com.perpetmatch.Domain.*;
import com.perpetmatch.Role.RoleRepository;
import com.perpetmatch.apiDto.Profile.PasswordRequest;
import com.perpetmatch.apiDto.Profile.ProfileRequest;
import com.perpetmatch.apiDto.UpdateMemberRequest;
import com.perpetmatch.exception.AppException;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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




    public boolean verifyingEmail(String token, String email) {

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return false;
        }

        if (!member.getEmailCheckToken().equals(token)) {
            return false;
        }
        member.completeSignup(token);
        return true;
    }

    public Member findByNickname(String nickname) {
        Member byNickname = memberRepository.findByNickname(nickname).
                orElseThrow(() -> new ResourceNotFoundException("Member", "nickname", nickname));

        return byNickname;
    }

    public void updateProfile(Long id, ProfileRequest profileRequest) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        member.setHouseType(profileRequest.getHouseType());
        member.setOccupation(profileRequest.getOccupation());
        member.setExperience(profileRequest.isExperience());
        member.setLiveAlone(profileRequest.isLiveAlone());
        member.setHowManyPets(profileRequest.getHowManyPets());
        member.setExpectedFeeForMonth(profileRequest.getExpectedFeeForMonth());
        member.setLocation(profileRequest.getLocation());
        member.setProfileImage(profileRequest.getProfileImage());
        member.setWantCheckUp(profileRequest.isWantCheckUp());
        member.setWantLineAgeImage(profileRequest.isWantLineAgeImage());
        member.setWantNeutered(profileRequest.isWantNeutered());
    }

    public void updatePassword(Long id, PasswordRequest passwordRequest) {

        Member member = memberRepository.findById(id).get();
        member.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
    }

    public void sendLoginLink(Member member) {
        member.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(member.getEmail());
        mailMessage.setSubject("퍼펫매치, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + member.getEmailCheckToken() + "&email=" + member.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void addPet(Long id, Pet pet) {
        Optional<Member> byId = memberRepository.findById(id);
        byId.ifPresent(m -> m.getPet().add(pet));
    }

    public void removePet(Long id, Pet pet) {
        Optional<Member> byId = memberRepository.findById(id);
        byId.ifPresent(m -> m.getPet().remove(pet));
    }
}
