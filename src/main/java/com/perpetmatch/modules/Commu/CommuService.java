package com.perpetmatch.modules.Commu;

import com.perpetmatch.api.dto.Commu.CommentDetailsDto;
import com.perpetmatch.exception.ResourceNotFoundException;
import com.perpetmatch.modules.Comment.CommentRepository;
import com.perpetmatch.Domain.Comment;
import com.perpetmatch.Domain.Commu;
import com.perpetmatch.Domain.User;
import com.perpetmatch.modules.Member.UserRepository;
import com.perpetmatch.api.dto.Commu.CommentDto;
import com.perpetmatch.api.dto.Commu.CommuPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommuService {

    private final UserRepository userRepository;
    private final CommuRepository commuRepository;
    private final CommentRepository commentRepository;

    public void createCommuBoard(Long id, CommuPostDto commuPostDto) {
        User user = userRepository.findById(id).orElseThrow((() -> new ResourceNotFoundException("Member", "id", id)));

        Commu commu = makeCommuBoard(user, commuPostDto);
        savedBoardToUser(user,commu);
    }

    private void savedBoardToUser(User user, Commu commu) {
        user.makeCommuBoard(commu);
    }

    public List<Commu> getAllBoards() {
        return commuRepository.findAllWithComment();
    }

    public Comment createCommentByUserId(Long curUserId, CommentDto dto) {
        User user = userRepository.findById(curUserId).orElseThrow((() -> new ResourceNotFoundException("User", "id", curUserId)));

        Comment comment = new Comment();
        comment.setNickname(user.getNickname());
        comment.setProfileImage(user.getProfileImage());
        comment.setText(dto.getText());
        return commentRepository.save(comment);

    }
    public void removeComment(Long commuBoardId, Long commentId) {
        Commu commu = commuRepository.findById(commuBoardId).orElseThrow(() -> new ResourceNotFoundException("CommuBoard", "id", commuBoardId));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        commu.removeComment(comment);

    }

    private Commu makeCommuBoard(User user, CommuPostDto commuPostDto) {

        Commu commu = new Commu();
        commu.setNickname(user.getNickname());
        commu.setImage(commuPostDto.getImage());
        commu.setChecked(commuPostDto.isChecked());
        commu.setDescription(commuPostDto.getDescription());
        commu.setLikes(commuPostDto.getLikes());
        commu.setProfileImage(user.getProfileImage());
        return commuRepository.save(commu);
    }

    @Transactional
    public void addLike(Long id) {
        Commu commu = commuRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Commu", "id", id));
        commu.addLikes();
    }

    @Transactional
    public void addToCommuBoard(Long commuId,Comment comment) {
        Commu commu = commuRepository.findById(commuId).orElseThrow((() -> new ResourceNotFoundException("Commu", "commuId", commuId)));
        commu.addComment(comment);
    }

    public Set<CommentDetailsDto> getComments(Long commuId) {
        Commu commu = commuRepository.findById(commuId).orElseThrow(() -> new ResourceNotFoundException("Commu", "commuId", commuId));
        return commu.getComments().stream().map(CommentDetailsDto::new).collect(Collectors.toSet());
    }
}
