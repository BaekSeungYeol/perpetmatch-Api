package com.perpetmatch.modules.Comment;

import com.perpetmatch.Domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
