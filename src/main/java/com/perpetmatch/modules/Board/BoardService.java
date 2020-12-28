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
    private final BoardMapper boardMapper;



    public Board findOneBoard(Long id) {
        return boardRepository.findZoneAndPetTitleAndPetAgeById(id);
    }

    public Board createNewBoard(Long id, BoardPostRequest boardRequest) {
        User member = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        Zone zone  = zoneRepository.findByProvince(boardRequest.getZone());
        PetAge age = calculatePetAge(boardRequest.getYear(), boardRequest.getMonth());
        Pet pet = petRepository.findByTitle(boardRequest.getPetTitle());

        Board board = boardMapper.createBoard(boardRequest, member, zone, pet, age);
        return boardRepository.save(board);
    }


    public void updateBoard(Long memberId, Long boardId, BoardUpdateRequest boardRequest) {

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId));
        User user = userRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));
        if(!board.getManager().equals(user.getNickname())) throw new UpdateException(user.getNickname());

        updateboardDetails(boardRequest, board);
    }

    private void updateboardDetails(BoardUpdateRequest boardRequest, Board board) {

        Zone zone  = zoneRepository.findByProvince(boardRequest.getZone());
        Pet pet = petRepository.findByTitle(boardRequest.getPetTitle());
        PetAge petAge = calculatePetAge(boardRequest.getYear(),boardRequest.getMonth());

        board.updateBoard(boardRequest,zone,pet,petAge);
    }

    private PetAge calculatePetAge(int year, int month) {
        for(int i=0; i< year; ++i) month += 12;
        if(month <= 12)
            return petAgeRepository.findPetRange("1년이하");
        else if(month > 12 && month < 84)
            return petAgeRepository.findPetRange("1년~7년");
        else
            return petAgeRepository.findPetRange("7년이상");
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

    public boolean isBoardApplied(Long id, String username) {
        Board board = boardRepository.findByIdWithUsers(id);
        return board.findAppliedUsers(username);
    }
}
