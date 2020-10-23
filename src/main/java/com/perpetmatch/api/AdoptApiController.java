package com.perpetmatch.api;

import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.Board.BoardService;
import com.perpetmatch.Domain.Board;
import com.perpetmatch.api.dto.Board.*;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdoptApiController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;

    @GetMapping("/boards/search")
    public ResponseEntity searchBoard(String keyword,
                                      @PageableDefault(size=9, page = 0, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AdoptBoard> boardList = boardRepository.findByKeyword(keyword, pageable);
        Slice<Board> allBoards = boardService.findAllBoards();
        Slice<AdoptBoardV1> changed = boardList.map(AdoptBoardV1::new);

        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "입양 게시판 검색입니다.", changed));
    }


    /**
     * 유저 프로필 기반 페이징 게시글 반환
     */
    @PostMapping("/boards/profile/search")
    public ResponseEntity searchByProfile(@RequestBody AdoptMatchDto matchDto,
                                          @PageableDefault(size=15, page = 0) Pageable pageable) {

        AdoptMatchCondition condition = boardService.toCondition(matchDto);
        Page<AdoptBoard> boardList = boardRepository.findByProfileKeyword(condition, pageable);
        Page<AdoptBoardV1> changed = boardList.map(AdoptBoardV1::new);
        // AdoptMatchCondition, AdoptMatchDto 참고
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "유저 기반 게시판 검색입니다.", changed));
    }

}
