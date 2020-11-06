package com.perpetmatch.Commu;

import com.perpetmatch.Comment.CommentRepository;
import com.perpetmatch.Domain.Comment;
import com.perpetmatch.Domain.Commu;
import com.perpetmatch.Domain.User;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.api.dto.Commu.CommentDto;
import com.perpetmatch.api.dto.Commu.CommuPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommuService {

    private final UserRepository userRepository;
    private final CommuRepository commuRepository;
    private final CommentRepository commentRepository;

    public void createCommuBoard(Long id, CommuPostDto commuPostDto) {

        makeCommuBoardAndSaveAdd(id, commuPostDto);

    }

    public List<Commu> getAllBoards() {
        return commuRepository.findAllWithComment();
    }

    public void createComments(Long currentMemberId, Long id, CommentDto dto) {
        User user = userRepository.findById(currentMemberId).get();

        Optional<Commu> commuBoard = commuRepository.findById(id);

        commuBoard.ifPresent(
                c -> {
                    Comment comment = new Comment();
                    comment.setNickname(user.getNickname());
                    comment.setProfileImage(user.getProfileImage());
                    comment.setText(dto.getText());
                    commentRepository.save(comment);
                    c.getComments().add(comment);
                }
        );
    }
    public void  removeComments(Long currentMemberId, Long boardId, Long commentId) {

        User user = userRepository.findById(currentMemberId).get();

        Optional<Commu> commuBoard = commuRepository.findById(boardId);

        commuBoard.ifPresent(
                c -> {
                    Comment comment = commentRepository.findById(commentId).get();
                    c.getComments().remove(comment);
                }
        );
    }

    private void makeCommuBoardAndSaveAdd(Long id, CommuPostDto commuPostDto) {
        User user = userRepository.findById(id).get();
        Commu commu = new Commu();
        commu.setNickname(user.getNickname());
        commu.setImage(user.getProfileImage());
        commu.setChecked(commuPostDto.isChecked());
        commu.setDescription(commuPostDto.getDescription());
        commu.setLikes(commuPostDto.getLikes());
        commu.setProfileImage(user.getProfileImage());
        Commu savedCommu = commuRepository.save(commu);

        user.getCommus().add(savedCommu);
    }
}
