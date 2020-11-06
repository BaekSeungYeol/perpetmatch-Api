package com.perpetmatch.api;

import com.perpetmatch.Comment.CommentRepository;
import com.perpetmatch.Commu.CommuRepository;
import com.perpetmatch.Commu.CommuService;
import com.perpetmatch.Domain.Comment;
import com.perpetmatch.Domain.Commu;
import com.perpetmatch.api.dto.Commu.CommentDetailsDto;
import com.perpetmatch.api.dto.Commu.CommentDto;
import com.perpetmatch.api.dto.Commu.CommuPostDto;
import com.perpetmatch.jjwt.CurrentMember;
import com.perpetmatch.jjwt.UserPrincipal;
import com.perpetmatch.jjwt.resource.ApiResponse;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commu")
@RequiredArgsConstructor
public class CommuController {

    private final CommuService commuService;
    private final CommentRepository commentRepository;
    private final CommuRepository commuRepository;

    @PostMapping("/boards")
    public ResponseEntity createBoard(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid CommuPostDto commuPostDto
            , Errors errors) {
        if(errors.hasErrors()) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 입력입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        if(currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        commuService.createCommuBoard(currentMember.getId(), commuPostDto);

        return ResponseEntity.ok().body(new ApiResponse(true, "소통 게시글이 등록 되었습니다."));
    }

    @PostMapping("/boards/{id}/comments")
    public ResponseEntity createComments(@CurrentMember UserPrincipal currentMember, @PathVariable Long id, @RequestBody  CommentDto dto) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        if(commuRepository.findById(id).isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        commuService.createComments(currentMember.getId(),id, dto);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 댓글을 추가했습니다."));
    }


    @DeleteMapping("/boards/{id}/comments/{commentId}")
    public ResponseEntity deleteComments(@CurrentMember UserPrincipal currentMember, @PathVariable Long id, @PathVariable Long commentId) {
        if (currentMember == null) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }
        if(commuRepository.findById(id).isEmpty() || commentRepository.findById(commentId).isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        commuService.removeComments(currentMember.getId(),id,commentId);

        return ResponseEntity.ok().body(new ApiResponse(true, "성공적으로 댓글을 제거했습니다."));
    }

    @GetMapping("/boards/{id}/comments")
    public ResponseEntity getComments(@PathVariable Long id) {
        if(commuRepository.findById(id).isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "잘못된 접근입니다."),
                    HttpStatus.BAD_REQUEST);
        }

        Commu commu = commuRepository.findById(id).get();
        Set<CommentDetailsDto> comments = commu.getComments().stream().map(CommentDetailsDto::new)
                .collect(Collectors.toSet());

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true,"소통 댓글 리스트 입니다.", comments));

    }

    @GetMapping("/boards")
    public ResponseEntity getBoards() {
        List<Commu> allCommuBoards = commuService.getAllBoards();
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true,"소통 리스트 입니다.", allCommuBoards));
    }


}
