package com.perpetmatch.api;

import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.Board.BoardService;
import com.perpetmatch.api.dto.Board.AdoptBoard;
import com.perpetmatch.api.dto.Board.AdoptMatchCondition;
import com.perpetmatch.api.dto.Board.AdoptMatchDto;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdoptApiController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;

    @GetMapping("/boards/v1/search")
    public ResponseEntity searchBoard(String keyword,
                                      @PageableDefault(size=9, page = 0, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AdoptBoard> boardList = boardRepository.findByKeyword(keyword, pageable);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "입양 게시판 검색입니다.", boardList));
    }


    /**
     * 유저 프로필 기반 페이징 게시글 반환
     */
    @PostMapping("/boards/profile/search")
    public ResponseEntity searchByProfile(AdoptMatchDto matchDto,Pageable pageable) {

        AdoptMatchCondition condition = boardService.toCondition(matchDto);

        // TODO service 로직 profile 기반 처리
        // AdoptMatchCondition, AdoptMatchDto 참고
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "유저 기반 게시판 검색입니다.",new AdoptBoard()));

    }

}
