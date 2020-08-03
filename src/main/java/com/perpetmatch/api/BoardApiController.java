package com.perpetmatch.api;

import com.perpetmatch.Board.BoardService;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.apiDto.Board.BoardData;
import com.perpetmatch.apiDto.Board.BoardRequest;
import com.perpetmatch.apiDto.Board.BoardResponse;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

//    @GetMapping("/board/one")
//    public ResponseEntity getOneBoard(@CurrentMember UserPrincipal currentMember) {
//
//    }
//
    @PostMapping("/board/one")
    public ResponseEntity createBoard(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid BoardRequest boardRequest
    , Errors errors) {
        if(errors.hasErrors()) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        Board newBoard = boardService.createNewBoard(currentMember.getId(), boardRequest);
        BoardResponse boardResponse = BoardResponse.builder().id(newBoard.getId()).title(newBoard.getTitle()).build();

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "게시글이 등록 되었습니다.",boardResponse));
    }



}
