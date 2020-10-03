package com.perpetmatch.Member;

import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.Domain.*;
import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.OrderItem.OrderItemRepository;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.Role.RoleRepository;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.api.dto.Board.ApplyUsers;
import com.perpetmatch.api.dto.Order.BagDetailsDto;
import com.perpetmatch.api.dto.Profile.PasswordRequest;
import com.perpetmatch.api.dto.Profile.ProfileRequest;
import com.perpetmatch.exception.AppException;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.infra.config.AppProperties;
import com.perpetmatch.infra.mail.EmailMessage;
import com.perpetmatch.infra.mail.EmailService;
import com.perpetmatch.jjwt.resource.SignUpRequest;
import com.perpetmatch.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;
    private final PetAgeRepository petAgeRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final BoardRepository boardRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;

    public void sendJoinMemberConfirmEmail(User savedMember) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + savedMember.getEmailCheckToken() + "&email="
                + savedMember.getEmail());
        context.setVariable("nickname", savedMember.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "퍼펫매치 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/link", context);


        EmailMessage emailMessage = EmailMessage.builder()
                .to(savedMember.getEmail())
                .subject("퍼펫매치, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);

    }

    public User join(SignUpRequest request) {

        User member = new User();
        member.setNickname(request.getNickname());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setEmail(request.getEmail());
        member.setJoinedAt(LocalDateTime.now());

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        member.setRoles(Collections.singleton(userRole));

        User savedMember = userRepository.save(member);

        savedMember.generateEmailCheckToken();
        sendJoinMemberConfirmEmail(savedMember);

        return savedMember;
    }


    private boolean validateDuplicateMember(User member) {
        List<User> findMembers = userRepository.findAllByNickname(member.getNickname());
        if (!findMembers.isEmpty()) {
            return true;
        }
        return false;
    }

    public User findOne(Long id) {
        Optional<User> byId = userRepository.findByIdWithTags(id);
        return byId.get();
    }

    public List<User> findMembers() {
        return userRepository.findAll();
    }


    public boolean verifyingEmail(String token, String email) {

        User member = userRepository.findByEmail(email);
        if (member == null) {
            return false;
        }

        if (!member.getEmailCheckToken().equals(token)) {
            return false;
        }
        member.completeSignup(token);
        return true;
    }

    public User findByParamId(Long id) {
        User member = userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));

        return member;
    }

    public User updateProfile(Long id, ProfileRequest profileRequest) {
        User member = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        member.setHouseType(profileRequest.getHouseType());
        member.setOccupation(profileRequest.getOccupation());
        member.setExperience(profileRequest.isExperience());
        member.setLiveAlone(profileRequest.isLiveAlone());
        member.setHasPet(profileRequest.isHasPet());
        member.setExpectedFeeForMonth(profileRequest.getExpectedFeeForMonth());
        member.setLocation(profileRequest.getLocation());
        member.setProfileImage(profileRequest.getProfileImage());
        member.setPhoneNumber(profileRequest.getPhoneNumber());
        member.setDescription(profileRequest.getDescription());
        member.setWantCheckUp(profileRequest.isWantCheckUp());
        member.setWantLineAge(profileRequest.isWantLineAge());
        member.setWantNeutered(profileRequest.isWantNeutered());
        return member;
    }

    public void updatePassword(Long id, PasswordRequest passwordRequest) {

        User member = userRepository.findById(id).get();
        member.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
    }

    public void sendLoginLink(User member) {

        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + member.getEmailCheckToken() + "&email=" + member.getEmail());
        context.setVariable("nickname", member.getNickname());
        context.setVariable("linkName", "이메일로 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(member.getEmail())
                .subject("퍼펫매치, 로그인 링크")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);

    }

    public void addPet(Long id, String title) {

        Pet pet = petRepository.findByTitle(title);

        if (pet == null) {
            Pet newPet = new Pet();
            newPet.setTitle(title);
            petRepository.save(newPet);
        }

        Pet savedPet = petRepository.findByTitle(title);

        Optional<User> byId = userRepository.findById(id);
        byId.ifPresent(m -> m.getPetTitles().add(savedPet));
    }

    public void removePet(Long id, Pet pet) {
        Optional<User> byId = userRepository.findById(id);
        byId.ifPresent(m -> m.getPetTitles().remove(pet));
    }

    public void addPetAge(Long id, String range) {

        PetAge petRange = petAgeRepository.findPetRange(range);
        Optional<User> byId = userRepository.findById(id);
        byId.ifPresent(m -> m.getPetAges().add(petRange));
    }

    public void removePetAge(Long id, String range) {
        Optional<User> byId = userRepository.findById(id);
        PetAge petRange = petAgeRepository.findPetRange(range);
        byId.ifPresent(m -> m.getPetAges().remove(petRange));
    }

    public User findById(Long id) {
        return null;
    }

    public void addZone(Long id, String province) {
        Zone zone = zoneRepository.findByProvince(province);
        Optional<User> byId = userRepository.findById(id);
        byId.ifPresent(m -> m.getZones().add(zone));
    }

    public void removeZone(Long id, String province) {
        Zone zone = zoneRepository.findByProvince(province);
        Optional<User> byId = userRepository.findById(id);
        byId.ifPresent(m -> m.getZones().remove(zone));
    }

    public boolean apply(Long id, String username) {
        Board board = boardRepository.findById(id).get();
        Optional<User> user = userRepository.findByNickname(username);
        user.ifPresent(u -> {
            if(board.isMember(u.getNickname()))
                board.removeMember(u);
            else
                board.addMember(u);
        });
        return board.isMember(username);
    }
    public List<ApplyUsers> applyUserList(Long id) {
        Board board = boardRepository.findById(id).get();
        List<ApplyUsers> users = board.getUsers().stream().map(u -> new ApplyUsers(u.getId(),u.getNickname(),u.getProfileImage(),u.getDescription()))
        .collect(Collectors.toList());
        return users;
    }

    public boolean isManager(String username, Long id) {
        Board board = boardRepository.findById(id).get();
        return board.isManager(username);
    }

    public boolean likes(Long id, String username) {
        Board board = boardRepository.findById(id).get();
        Optional<User> user = userRepository.findByNickname(username);
        user.ifPresent(u -> {
            if(u.getLikeList().contains(board))
                u.removeLikeBoard(board);
            else
                u.addLikeBoard(board);
        });
        return user.get().hasLikeBoard(board);
    }

    public void addBag(Long userId, Long id, int count) {
        User user = userRepository.findByIdWithBags(userId);
        Item item = itemRepository.findById(id).get();
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(item.getPrice());
        orderItem.setCount(count);

        orderItemRepository.save(orderItem);
        user.getBag().add(orderItem);
    }

    public void removeBag(Long id, Long orderItemId) {
        User user = userRepository.findByIdWithBags(id);
        OrderItem orderItem = orderItemRepository.findById(orderItemId).get();
        user.getBag().remove(orderItem);
    }

    public Set<BagDetailsDto> getBags(Long id) {
        User user = userRepository.findByIdWithBags(id);
        Set<BagDetailsDto> collect = user.getBag().stream().map(o -> new BagDetailsDto(o.getId(),o.getItem().getName(), o.getOrderPrice(), o.getCount())).collect(Collectors.toSet());
        return collect;
    }

    public int getTotalSum(Long id) {
        User user = userRepository.findByIdWithBags(id);
        int sum = 0;
        for(OrderItem o : user.getBag())
            sum += o.getTotalPrice();

        return sum;
    }
}
