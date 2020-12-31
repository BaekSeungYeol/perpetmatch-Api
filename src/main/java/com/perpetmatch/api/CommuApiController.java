package com.perpetmatch.api;

import com.perpetmatch.Domain.Comment;
import com.perpetmatch.jjwt.resource.ApiResponseDto;
import com.perpetmatch.modules.Comment.CommentRepository;
import com.perpetmatch.modules.Commu.CommuRepository;
import com.perpetmatch.modules.Commu.CommuService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping("/api/commu")
@RequiredArgsConstructor
public class CommuApiController {

    private final CommuService commuService;

    @PostMapping("/boards/{id}/likes")
    public ResponseEntity likes(@CurrentMember UserPrincipal currentMember, @PathVariable Long id) {
        if(currentMember == null) return ResponseEntity.ok().body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        commuService.addLike(id);

        return ResponseEntity.ok().body(ApiResponseDto.createOK());
    }


    @PostMapping("/boards")
    public ResponseEntity createBoard(@CurrentMember UserPrincipal currentMember, @RequestBody @Valid CommuPostDto commuPostDto
            , Errors errors) {
        if(currentMember == null) return ResponseEntity.ok().body(ApiResponseDto.DEFAULT_UNAUTHORIZED);
        if(errors.hasErrors()) return ResponseEntity.ok().body(ApiResponseDto.badRequest());

        commuService.createCommuBoard(currentMember.getId(), commuPostDto);

        return ResponseEntity.ok().body(ApiResponseDto.createOK());
    }

    @PostMapping("/boards/{id}/comments")
    public ResponseEntity createComments(@CurrentMember UserPrincipal currentMember, @PathVariable Long id, @RequestBody  CommentDto dto) {
        if(currentMember == null) return ResponseEntity.ok().body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        Comment comment = commuService.createCommentByUserId(currentMember.getId(), dto);
        commuService.addToCommuBoard(id,comment);

        return ResponseEntity.ok().body(ApiResponseDto.createOK());
    }


    @DeleteMapping("/boards/{id}/comments/{commentId}")
    public ResponseEntity deleteComments(
            @CurrentMember UserPrincipal currentMember, @PathVariable Long id, @PathVariable Long commentId) {
        if (currentMember == null) return ResponseEntity.ok().body(ApiResponseDto.DEFAULT_UNAUTHORIZED);

        commuService.removeComment(id,commentId);

        return ResponseEntity.ok().body(ApiResponseDto.createOK());
    }

    @GetMapping("/boards/{id}/comments")
    public ResponseEntity getComments(@PathVariable Long id) {

        Set<CommentDetailsDto> comments = commuService.getComments(id);

        return ResponseEntity.ok().body(ApiResponseDto.createOK(comments));

    }

    @GetMapping("/boards")
    public ResponseEntity getBoards() {
        List<Commu> allCommuBoards = commuService.getAllBoards();
        return ResponseEntity.ok().body(ApiResponseDto.createOK(allCommuBoards));
    }


}
