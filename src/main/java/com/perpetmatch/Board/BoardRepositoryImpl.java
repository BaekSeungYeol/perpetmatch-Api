package com.perpetmatch.Board;

import com.perpetmatch.Domain.Board;
import com.perpetmatch.Domain.QBoard;
import com.perpetmatch.api.dto.Board.AdoptBoard;
import com.perpetmatch.api.dto.Board.QAdoptBoard;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.perpetmatch.Domain.QBoard.*;

public class BoardRepositoryImpl extends QuerydslRepositorySupport implements BoardRepositoryCustom{

    public BoardRepositoryImpl() {
        super(Board.class);
    }

    @Override
    public List<AdoptBoard> findByKeyword(String keyword) {
        JPQLQuery<AdoptBoard> result = from(board)
                .where(board.title.containsIgnoreCase(keyword)
                        .or(board.petTitle.title.containsIgnoreCase(keyword))
                        .or(board.zone.province.containsIgnoreCase(keyword))
                        .or(board.petAge.petRange.containsIgnoreCase(keyword)))
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
                        board.boardImage1));

        return result.fetch();
    }
}
