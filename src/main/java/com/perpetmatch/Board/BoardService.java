package com.perpetmatch.Board;

import com.perpetmatch.Domain.Board;
import com.perpetmatch.Zone.ZoneRepository;
import com.perpetmatch.pet.PetRepository;
import com.perpetmatch.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository userRepository;
    private final PetRepository petRepository;
    private final ZoneRepository zoneRepository;

//    public List<Board> findAllBoard() {
//
//    }

    @Transactional
    public Long create(Board board) {


        Board save = boardRepository.save(board);

        return save.getId();
    }
}
