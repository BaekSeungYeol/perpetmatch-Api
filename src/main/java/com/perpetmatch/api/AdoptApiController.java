package com.perpetmatch.api;

import com.perpetmatch.modules.Board.BoardRepository;
import com.perpetmatch.modules.Board.BoardService;
import com.perpetmatch.api.dto.Board.*;
import com.perpetmatch.jjwt.resource.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdoptApiController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;

    @GetMapping("/boards/search")
    public ResponseEntity searchBoard(String keyword,
                                      @PageableDefault(size=9, page = 0, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<AdoptBoard> boardList = boardRepository.findByKeyword(keyword, pageable);
        Slice<AdoptBoardV1> boardListWithKeyword = boardList.map(AdoptBoardV1::new);

        return ResponseEntity.ok().body(ApiResponseDto.createOK(boardListWithKeyword));
    }


    /**
     * 유저 프로필 기반 페이징 게시글 반환
     */
    @PostMapping("/boards/profile/search")
    public ResponseEntity searchByProfile(@RequestBody AdoptMatchDto matchDto,
                                          @PageableDefault(size=15, page = 0) Pageable pageable) {

        AdoptMatchCondition condition = boardService.makeCondition(matchDto);
        Page<AdoptBoard> boardList = boardRepository.findByProfileTags(condition, pageable);
        Page<AdoptBoardV1> boardListWithProfile = boardList.map(AdoptBoardV1::new);
        return ResponseEntity.ok().body(ApiResponseDto.createOK(boardListWithProfile));
    }

}
