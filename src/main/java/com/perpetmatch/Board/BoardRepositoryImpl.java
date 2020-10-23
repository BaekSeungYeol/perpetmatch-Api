package com.perpetmatch.Board;

import com.perpetmatch.Domain.*;
import com.perpetmatch.api.dto.Board.AdoptBoard;
import com.perpetmatch.api.dto.Board.AdoptMatchCondition;
import com.perpetmatch.api.dto.Board.QAdoptBoard;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Predicates.in;
import static com.perpetmatch.Domain.QBoard.*;
import static com.perpetmatch.Domain.QUser.*;
import static org.springframework.util.StringUtils.hasText;

@Transactional
public class BoardRepositoryImpl extends QuerydslRepositorySupport implements BoardRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(EntityManager em) {
        super(Board.class);
        this.queryFactory = new JPAQueryFactory(em);
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
                        board.hasLineAge,
                        board.hasNeutered,
                        board.description,
                        board.boardImage1,
                        board.createdAt
                ));

        JPQLQuery<AdoptBoard> resultP = getQuerydsl().applyPagination(pageable, result);
        QueryResults<AdoptBoard> fetchResults = resultP.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }


    /*
               public List<Movie> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QMovie movie = QMovie.movie;
        JPQLQuery<Movie> query = from(movie).where(movie.published.isTrue()
                .and(movie.closed.isFalse())
                .and(movie.tags.any().in(tags))
                .and(movie.zones.any().in(zones)))
                .leftJoin(movie.tags, QTag.tag).fetchJoin()
                .leftJoin(movie.zones, QZone.zone).fetchJoin()
                .orderBy(movie.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }
     */

    private BooleanExpression checkUpEq(Boolean hasCheckUp) {
        return hasCheckUp ? board.hasCheckUp.eq(true) : null;
    }
    private BooleanExpression lineAgeEq(Boolean hasLineAge) {
        return hasLineAge ? board.hasLineAge.eq(true) : null;
    }
    private BooleanExpression neuteredEq(Boolean neutered) {
        return neutered ? board.hasNeutered.eq(true) : null;
    }
    private BooleanExpression creditLoe(Integer credit) {
        return credit != null ? board.credit.loe(credit) : null;
    }

    @Override
    public Page<AdoptBoard> findByProfileKeyword(AdoptMatchCondition condition, Pageable pageable) {
        QueryResults<AdoptBoard> results = queryFactory
                .select(new QAdoptBoard(
                        board.id.as("id"),
                        board.title,
                        board.credit,
                        board.zone.province,
                        board.year,
                        board.month,
                        board.petTitle.title,
                        board.petAge.petRange,
                        board.hasCheckUp,
                        board.hasLineAge,
                        board.hasNeutered,
                        board.description,
                        board.boardImage1,
                        board.createdAt
                ))
                .from(board)
                .leftJoin(board.petTitle, QPet.pet)
                .leftJoin(board.zone, QZone.zone)
                .leftJoin(board.petAge, QPetAge.petAge)
                .where(
                       board.petTitle.in(condition.getPetTitles())
                        .or(board.petAge.in(condition.getPetAges()))
                        .or(board.zone.in(condition.getZones()))
                        .or(checkUpEq(condition.isWantCheckUp()))
                        .or(lineAgeEq(condition.isWantLineAge()))
                        .or(neuteredEq(condition.isWantNeutered()))
                        .or(creditLoe(condition.getExpectedFeeForMonth())))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<AdoptBoard> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content,pageable,total);
    }
}
