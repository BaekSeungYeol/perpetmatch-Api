package com.perpetmatch.Board;

import com.perpetmatch.Domain.Board;
import com.perpetmatch.api.dto.Board.AdoptBoard;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BoardRepositoryCustom {
    List<AdoptBoard> findByKeyword(String keyword);
}
