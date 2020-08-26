package com.perpetmatch.Board;

import com.perpetmatch.Domain.*;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
import com.perpetmatch.api.dto.Board.BoardUpdateRequest;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.exception.UpdateException;
import com.perpetmatch.pet.PetRepository;
import com.perpetmatch.Member.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        board.setCheckUp(boardRequest.getCheckUp());
        board.setLineAgeImage(boardRequest.getLineAgeImage());
        board.setNeuteredImage(boardRequest.getNeuteredImage());
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

        board.setCheckUp(boardRequest.getCheckUp());
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
        PageRequest pg = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "publishedDateTime"));
        return boardRepository.findAllBoards(pg);
    }
}
