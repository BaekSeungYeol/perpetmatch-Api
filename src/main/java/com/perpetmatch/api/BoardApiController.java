package com.perpetmatch.api;

import com.perpetmatch.Board.BoardService;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.Member;
import com.perpetmatch.Domain.Pet;
import com.perpetmatch.Domain.Zone;
import com.perpetmatch.Member.MemberService;
import com.perpetmatch.apiDto.BoardRequest;
import com.perpetmatch.pet.PetService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;
    private final PetService petService;
    private final MemberService memberService;

    @PostMapping("/api/boards")
    public ResponseEntity createBoard(@RequestBody @Valid BoardRequest boardRequest) {

        Board board = Board.builder()
                .title(boardRequest.getTitle())
                .image(boardRequest.getImage())
                .publishedDateTime(boardRequest.getPublishedDateTime())
                .description(boardRequest.getDescription())
                .credit(boardRequest.getCredit())
                .neutered(boardRequest.isNeutered())
                .build();

        boardService.create(board);

        return ResponseEntity.ok().build();
    }



//    @GetMapping("/api/boards")
//    public BoardListResult findAllBoards() {
//
//        List<Board> boards = boardService.findAllBoard();
//        List<BoardListDto> collect =
//
//    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class BoardListDto {

        String manager;
        private Set<Member> members = new HashSet<>();
        private String title;
        private String description;
        private int credit;
        private String image;
        private boolean neutered;
        private LocalDateTime publishedDateTime;
        private Pet pet;
        private Zone zone;

        public BoardListDto(Board board) {
            this.manager = board.getManager();
            this.members = board.getMembers();
            this.title = title;
            this.description = description;
            this.credit = credit;
            this.image = image;
            this.neutered = neutered;
            this.publishedDateTime = publishedDateTime;
            this.pet = pet;
            this.zone = zone;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class BoardListResult<T> {
        private T data;
    }


}
