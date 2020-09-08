package com.perpetmatch.api;

import com.perpetmatch.Board.BoardRepository;
import com.perpetmatch.api.dto.Board.AdoptBoard;
import com.perpetmatch.jjwt.resource.ApiResponseWithData;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity searchBoard(String keyword) {
        List<AdoptBoard> boardList = boardRepository.findByKeyword(keyword);
        return ResponseEntity.ok().body(new ApiResponseWithData<>(true, "입양 게시판 검색입니다.", boardList));

    }

}
