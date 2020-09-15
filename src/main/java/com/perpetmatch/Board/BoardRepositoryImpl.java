package com.perpetmatch.Board;

import com.perpetmatch.Domain.*;
import com.perpetmatch.api.dto.Board.AdoptBoard;
import com.perpetmatch.api.dto.Board.QAdoptBoard;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.perpetmatch.Domain.QBoard.*;

public class BoardRepositoryImpl extends QuerydslRepositorySupport implements BoardRepositoryCustom{

    public BoardRepositoryImpl() {
        super(Board.class);
    }

    @Override
    public Page<AdoptBoard> findByKeyword(String keyword, Pageable pageable) {
        JPQLQuery<AdoptBoard> result = from(board)
                .where(board.title.containsIgnoreCase(keyword)
                        .or(board.petTitle.title.containsIgnoreCase(keyword))
                        .or(board.zone.province.containsIgnoreCase(keyword))
                        .or(board.petAge.petRange.containsIgnoreCase(keyword)))
                .leftJoin(board.petTitle, QPet.pet)
                .leftJoin(board.zone, QZone.zone)
                .leftJoin(board.petAge, QPetAge.petAge)
                .select(new QAdoptBoard(
                        board.id,
                        board.title,
                        board.credit,
                        board.zone.province,
                        board.year,
                        board.month,
                        board.petTitle.title,
                        board.petAge.petRange,
                        board.hasCheckUp,
                        board.hasLineAgeImage,
                        board.neutered,
                        board.description,
                        board.boardImage1,
                        board.createdAt
                ));

        JPQLQuery<AdoptBoard> resultP = getQuerydsl().applyPagination(pageable, result);
        QueryResults<AdoptBoard> fetchResults = resultP.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
