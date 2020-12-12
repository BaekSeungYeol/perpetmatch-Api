package com.perpetmatch.modules.Board;

import com.perpetmatch.Domain.*;
import com.perpetmatch.modules.PetAge.PetAgeRepository;
import com.perpetmatch.modules.Zone.ZoneRepository;
import com.perpetmatch.api.dto.Board.AdoptMatchCondition;
import com.perpetmatch.api.dto.Board.AdoptMatchDto;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
import com.perpetmatch.api.dto.Board.BoardUpdateRequest;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.exception.UpdateException;
import com.perpetmatch.modules.pet.PetRepository;
import com.perpetmatch.modules.Member.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final ZoneRepository zoneRepository;
    private final PetAgeRepository petAgeRepository;

    public Board createNewBoard(Long id, BoardPostRequest boardRequest) {
        User member = userRepository.findById(id).get();

        Board board = new Board();
        board.addManager(member);
        board.setTitle(boardRequest.getTitle());
        board.setCredit(boardRequest.getCredit());

        String city = boardRequest.getZone();
        Zone zone  = zoneRepository.findByProvince(city);
        board.setZone(zone);
        board.setGender(boardRequest.getGender());
        board.setYear(boardRequest.getYear());
        board.setMonth(boardRequest.getMonth());


        PetAge petAge = calculatePetAge(boardRequest);
        board.setPetAge(petAge);

        Pet byTitle = petRepository.findByTitle(boardRequest.getPetTitle());
        board.setPetTitle(byTitle);

        if(!boardRequest.getCheckUpImage().isEmpty()){
            board.setCheckUpImage(boardRequest.getCheckUpImage());
            board.setHasCheckUp(true);
        }
        if(!boardRequest.getLineAgeImage().isEmpty()){
            board.setLineAgeImage(boardRequest.getLineAgeImage());
            board.setHasLineAge(true);
        }
        board.setHasNeutered(boardRequest.getHasNeutered());

        board.setDescription(boardRequest.getDescription());
        board.setBoardImage1(boardRequest.getBoardImage1());
        board.setBoardImage2(boardRequest.getBoardImage2());
        board.setBoardImage3(boardRequest.getBoardImage3());
        board.setPublishedDateTime(LocalDateTime.now());

        Board savedBoard = boardRepository.save(board);
        return savedBoard;
    }

    public Board findByBoardId(Long id) {
        Board board = boardRepository.findZoneAndPetTitleAndPetAgeById(id);
        return board;
    }

    public void updateBoard(Long memberId, Long boardId, BoardUpdateRequest boardRequest) {

        Board board = boardRepository.findById(boardId).
                orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId));

        User user = userRepository.findById(memberId).
              orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));

        if(!board.getManager().equals(user.getNickname())){
            throw new UpdateException(user.getNickname());
        }

        board.setCredit(boardRequest.getCredit());

        String city = boardRequest.getZone();
        Zone zone  = zoneRepository.findByProvince(city);
        board.setZone(zone);
        board.setGender(boardRequest.getGender());
        board.setYear(boardRequest.getYear());
        board.setMonth(boardRequest.getMonth());

        PetAge petAge = calculatePetAge(boardRequest);
        board.setPetAge(petAge);

        Pet byTitle = petRepository.findByTitle(boardRequest.getPetTitle());
        board.setPetTitle(byTitle);

        board.setCheckUpImage(boardRequest.getCheckUp());
        board.setLineAgeImage(boardRequest.getLineAgeImage());
        board.setNeuteredImage(boardRequest.getNeuteredImage());
        board.setDescription(boardRequest.getDescription());
        board.setBoardImage1(boardRequest.getBoardImage1());
        board.setBoardImage2(boardRequest.getBoardImage2());
        board.setBoardImage3(boardRequest.getBoardImage3());
    }

    private PetAge calculatePetAge(BoardUpdateRequest boardRequest) {
        PetAge petAge;

        int month = boardRequest.getMonth();
        for(int i=0; i< boardRequest.getYear(); ++i)
            month += 12;

        if(month <= 12)
            petAge = petAgeRepository.findPetRange("1년이하");
        else if(month > 12 && month < 84)
            petAge = petAgeRepository.findPetRange("1년~7년");
        else
            petAge = petAgeRepository.findPetRange("7년이상");
        return petAge;
    }
    private PetAge calculatePetAge(BoardPostRequest boardRequest) {
        PetAge petAge;

        int month = boardRequest.getMonth();
        for(int i=0; i< boardRequest.getYear(); ++i)
            month += 12;

        if(month <= 12)
            petAge = petAgeRepository.findPetRange("1년이하");
        else if(month > 12 && month < 84)
            petAge = petAgeRepository.findPetRange("1년~7년");
        else
            petAge = petAgeRepository.findPetRange("7년이상");
        return petAge;
    }

    public Slice<Board> findAllBoards() {
        PageRequest pg = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "publishedDateTime"));
        return boardRepository.findAllBoards(pg);
    }
    public List<Board> findAllWith() {
        return boardRepository.findAllWith();
    }

    public AdoptMatchCondition makeCondition(AdoptMatchDto matchDto) {
        AdoptMatchCondition condition = makeSearchCondition(matchDto);
        return condition;
    }

    private AdoptMatchCondition makeSearchCondition(AdoptMatchDto matchDto) {
        AdoptMatchCondition condition = new AdoptMatchCondition();
        List<String> zones = matchDto.getZones();
        List<String> petTitle = matchDto.getPetTitles();
        List<String> petAge = matchDto.getPetAges();

        if(zones != null) {
            for (String zone : zones) {
                Zone byProvince = zoneRepository.findByProvince(zone);
                condition.getZones().add(byProvince);
            }
        }
        if(petTitle != null) {
            for (String s : petTitle) {
                Pet pet = petRepository.findByTitle(s);
                condition.getPetTitles().add(pet);
            }
        }
        if(petAge != null) {
            for (String s : petAge) {
                PetAge pAge = petAgeRepository.findPetRange(s);
                condition.getPetAges().add(pAge);
            }
        }

        condition.setWantCheckUp(matchDto.isWantCheckUp());
        condition.setWantLineAge(matchDto.isWantLineAge());
        condition.setWantNeutered(matchDto.isWantNeutered());
        condition.setExpectedFeeForMonth(matchDto.getExpectedFeeForMonth());
        return condition;
    }

    public boolean isApplied(Long id, String username) {
        Board board = boardRepository.findByIdWithUsers(id);
        Set<User> users = board.getUsers();

        for(User u : users) {
            if(u.getNickname().equals(username)) return true;
        }

        return false;
   }
}