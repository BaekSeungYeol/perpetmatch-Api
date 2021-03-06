package com.perpetmatch.AdoptBoard.domain;

import com.perpetmatch.api.dto.Board.AdoptBoard;
import com.perpetmatch.api.dto.Board.AdoptMatchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepositoryCustom {
    Page<AdoptBoard> findByKeyword(String keyword, Pageable pageable);
    Page<AdoptBoard> findByProfileTags(AdoptMatchCondition condition, Pageable pageable);
}
