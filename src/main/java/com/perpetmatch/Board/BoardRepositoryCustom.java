package com.perpetmatch.Board;

import com.perpetmatch.api.dto.Board.AdoptBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BoardRepositoryCustom {
    Page<AdoptBoard> findByKeyword(String keyword, Pageable pageable);
}
