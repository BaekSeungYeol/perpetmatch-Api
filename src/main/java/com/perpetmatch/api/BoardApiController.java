package com.perpetmatch.api;

import com.perpetmatch.jjwt.resource.ApiResponseCode;
import com.perpetmatch.jjwt.resource.ApiResponseDto;
import com.perpetmatch.modules.Board.BoardRepository;
import com.perpetmatch.modules.Board.BoardService;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.User;
import com.perpetmatch.modules.Member.UserService;
import com.perpetmatch.api.dto.Board.*;
import com.perpetmatch.api.dto.User.UserCredit;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final UserService userService;


    @GetMapping("/boards")
    public ResponseEntity getAdoptBoardLists(Pageable pageable) {
        Slice<Board> AdoptBoardEntityLists = boardService.findAllBoards();
        Slice<BoardPageData> AdoptBoardDtoLists = AdoptBoardEntityLists.map(BoardPageData::new);
        return ResponseEntity.ok().body(ApiResponseDto.createOK(AdoptBoardDtoLists));
    }


    @GetMapping("/boards/{id}")
    public ResponseEntity getOneAdoptBoardList(@PathVariable Long id) {

        if (!boardRepository.existsById(id)) return ResponseEntity.ok().body(ApiResponseCode.BAD_PARAMETER);

        Board board = boardService.findOneBoard(id);
        AdoptBoardV2 OneBoardList = new AdoptBoardV2(board);

        return ResponseEntity.ok().body(ApiResponseDto.createOK(OneBoardList));
    }

    @PostMapping("/boards")
    public ResponseEntity createBoard(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid BoardPostRequest boardRequest
    , Errors errors) {
        if(errors.hasErrors() || currentMember == null) return ResponseEntity.ok().body(ApiResponseCode.BAD_PARAMETER);

        Board board = boardService.createNewBoard(currentMember.getId(), boardRequest);
        BoardResponse createdBoardIdAndTitle = BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .build();

        return ResponseEntity.ok().body(ApiResponseDto.createOK(createdBoardIdAndTitle));
    }


    @PutMapping("/boards/{id}")
    public ResponseEntity updateBoard(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                      @RequestBody @Valid BoardUpdateRequest boardRequest, Errors errors) {

        if(errors.hasErrors()) return ResponseEntity.ok().body(ApiResponseCode.BAD_PARAMETER);

        boardService.updateBoard(currentMember.getId(), id, boardRequest);

        Board board = boardService.findOneBoard(id);
        BoardResponse createdBoardIdAndTitle = BoardResponse.builder().id(board.getId()).title(board.getTitle()).build();

        return ResponseEntity.ok().body(ApiResponseDto.createOK(createdBoardIdAndTitle));
    }


    /**
     * 신청된 회원들은 글을 등록한 유저만(isManager) 표시가 되야 하며
     * 입양하기 게시글 클릭시 보여지도록 하여야 한다.
     * 이떄 보여주어야 할 것은 일단 image와 닉네임
     */
    @GetMapping("/boards/{id}/manager")
    public ResponseEntity isManager(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String username = currentMember.getUsername();
        boolean isManager = userService.isManager(username, id);

        if(!isManager)  return new ResponseEntity<>(new ApiResponse(false, "글의 주인이 아닙니다."), HttpStatus.BAD_REQUEST);
        else {
            List<ApplyUsers> applyUsers = userService.applyUserList(id);
            BoardApplyUsers boardResponse = new BoardApplyUsers(applyUsers);
            return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "현재 신청한 유저 목록입니다.",boardResponse));
        }

    }


    /**
     * 수락 버튼을 누를 시 결제가 되야 한다.
     * 여기서 id는 board의 id
     */
    @PostMapping("/boards/{id}/accept")
    public ResponseEntity accept(@CurrentMember UserPrincipal currentMember, @PathVariable Long id,
                                 @RequestBody NameDto name) {
        if(currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        String username = currentMember.getUsername();
        boolean isManager = userService.isManager(username, id);

        if(!isManager)  return new ResponseEntity<>(new ApiResponse(false, "글의 주인이 아닙니다."), HttpStatus.BAD_REQUEST);


        User user = userService.acceptUser(id, name);
        UserCredit userCredit = new UserCredit(user);

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "수락이 완료되었습니다.",userCredit));
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
        boolean userIn = userService.apply(id, username);

        // 현재 글에(id) 신청한 멤버의 id와 username 보여주기
        BoardApply boardResponse = new BoardApply(userIn);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "현재 신청한 유저 여부입니다.",boardResponse));
    }

    /**
     * 관심글 등록을 누를 시
     */
    @PostMapping("/boards/{id}/likes")
    public ResponseEntity likes(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        String username = currentMember.getUsername();
        boolean likeApply = userService.likes(id, username);

        // 현재 글에(id) 신청한 멤버의 id와 username 보여주기
        BoardLike boardResponse = new BoardLike(likeApply);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "현재 유저의 즐겨찾기 여부입니다. ",boardResponse));
    }


    /**
     * 신청하기가 되었는지 조회
     * applied_me
     */
    @GetMapping("/boards/{id}/applied_me")
    public ResponseEntity isAppliedUser(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        boolean applied = boardService.isApplied(id, currentMember.getUsername());

        if(applied)
            return ResponseEntity.ok().body(new ApiResponse(true, "현재 신청된 유저입니다."));
        else
            return ResponseEntity.ok().body(new ApiResponse(false, "현재 신청하지 않은 유저입니다."));

    }



}
