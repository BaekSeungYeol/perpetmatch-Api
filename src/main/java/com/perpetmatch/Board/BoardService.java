package com.perpetmatch.Board;

import com.perpetmatch.Domain.*;
import com.perpetmatch.PetAge.PetAgeRepository;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.apiDto.Board.BoardRequest;
import com.perpetmatch.pet.PetRepository;
import com.perpetmatch.Member.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final ZoneRepository zoneRepository;
    private final PetAgeRepository petAgeRepository;

    public Board createNewBoard(Long id, BoardRequest boardRequest) {
        User member = userRepository.findById(id).get();

        Board board = new Board();
        board.addManager(member);
        board.setTitle(boardRequest.getTitle());
        board.setCredit(boardRequest.getCredit());

        String[] city = boardRequest.getZone().split(",");
        Zone zone  = zoneRepository.findByProvince(city[1]);
        board.setZone(zone);
        board.setGender(boardRequest.getGender());
        board.setYear(boardRequest.getYear());
        board.setMonth(boardRequest.getMonth());


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


        board.setPetAge(petAge);

        Pet byTitle = petRepository.findByTitle(boardRequest.getTitle());
        board.setPet(byTitle);

        board.setCheckUp(boardRequest.getCheckUp());
        board.setLineAgeImage(boardRequest.getLineAgeImage());
        board.setNeuteredImage(boardRequest.getNeuteredImage());
        board.setDescription(boardRequest.getDescription());
        board.setBoardImage1(boardRequest.getBoardImage1());
        board.setBoardImage2(boardRequest.getBoardImage2());
        board.setBoardImage3(boardRequest.getBoardImage3());

        Board savedBoard = boardRepository.save(board);
        return savedBoard;
    }
}
