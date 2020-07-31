package com.perpetmatch.Board;

import com.perpetmatch.Domain.*;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.apiDto.Board.BoardRequest;
import com.perpetmatch.pet.PetRepository;
import com.perpetmatch.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;

    public Board createNewBoard(Long id, BoardRequest boardRequest) {
        Member member = memberRepository.findById(id).get();

        Board board = new Board();
        board.addManager(member);
        board.setTitle(boardRequest.getTitle());
        board.setCredit(boardRequest.getCredit());

        String[] city = boardRequest.getZone().split(",");
        Zone zone = Zone.builder().city(city[0]).province(city[1]).build();
        board.setZone(zone);
        board.setGender(boardRequest.getGender());
        board.setYear(boardRequest.getYear());
        board.setMonth(boardRequest.getMonth());

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
