package com.perpetmatch.api;

import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.api.dto.Board.AdoptBoard;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdoptApiController {

    private final BoardRepository boardRepository;

    @GetMapping("/boards/v1/search")
    public ResponseEntity searchBoard(String keyword,
                                      @PageableDefault(size=9, page = 0, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AdoptBoard> boardList = boardRepository.findByKeyword(keyword, pageable);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "입양 게시판 검색입니다.", boardList));
    }

}
