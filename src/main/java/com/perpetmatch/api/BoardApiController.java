package com.perpetmatch.api;

import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.Board.BoardService;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.User;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.api.dto.Board.*;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * 입양하기 페이지 다건 조회
     */
    @GetMapping("/boards")
    public ResponseEntity getBoards(Pageable pageable) {
        Slice<Board> allBoards = boardService.findAllBoards();
        Slice<BoardPageData> map = allBoards.map(board -> new BoardPageData(board));

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "게시글 다건 조회입니다.", map));
    }


    // 단건 조회
    @GetMapping("/boards/{id}")
    public ResponseEntity getOneBoard(@PathVariable Long id) {

        if (!boardRepository.existsById(id)) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        Board board = boardService.findByBoardId(id);

        BoardGetResponseV1 boardResponse = new BoardGetResponseV1(board);

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "해당 유저의 게시글입니다.", boardResponse));
    }

    @PostMapping("/boards")
    public ResponseEntity createBoard(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid BoardPostRequest boardRequest
    , Errors errors) {
        if(errors.hasErrors()) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 입력입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        if(currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        Board newBoard = boardService.createNewBoard(currentMember.getId(), boardRequest);
        BoardResponse boardResponse = BoardResponse.builder().id(newBoard.getId()).title(newBoard.getTitle()).build();

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "게시글이 등록 되었습니다.",boardResponse));
    }

    @PutMapping("/boards/{id}")
    public ResponseEntity updateBoard(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                      @RequestBody @Valid BoardUpdateRequest boardRequest, Errors errors) {
        if(errors.hasErrors()) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 입력입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        boardService.updateBoard(currentMember.getId(), id, boardRequest);
        Board board = boardService.findByBoardId(id);
        BoardGetResponseV1 boardResponse = new BoardGetResponseV1(board);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "게시글 수정이 완료되었습니다.", boardResponse));

    }

    /**
     * 신청하기를 누를 시
     * 없다면 신청이 되고 신청이 된 상태에서 다시 한번 누르면 신청이 취소된다.
     */
    @PostMapping("/boards/{id}/apply")
    public ResponseEntity apply(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String username = currentMember.getUsername();
        List<String> applyUsers = userService.apply(id, username);

        // 현재 글에(id) 신청된 멤버들(users)를 보여주기
        BoardApplyUsers boardResponse = new BoardApplyUsers(applyUsers);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "현재 신청한 유저 목록입니다.",boardResponse));
    }
    /**
     * 관심글 등록을 누를 시
     */

}
