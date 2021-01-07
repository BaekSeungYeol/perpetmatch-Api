package com.perpetmatch.modules.Board;

import com.perpetmatch.Domain.*;
import com.perpetmatch.api.dto.Board.BoardPostRequest;
import com.perpetmatch.modules.Member.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BoardMapper {

    public Board createBoard(BoardPostRequest boardRequest, User user, Zone zone, Pet pet, PetAge age) {
        Board board = new Board();
        board.addManager(user);
        board.setTitle(boardRequest.getTitle());
        board.setCredit(boardRequest.getCredit());
        board.setZone(zone);
        board.setGender(boardRequest.getGender());
        board.setYear(boardRequest.getYear());
        board.setMonth(boardRequest.getMonth());
        board.setPetAge(age);
        board.setPetTitle(pet);
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
        return board;
    }
}