package com.perpetmatch.Comment;

import com.perpetmatch.Domain.Comment;
import com.perpetmatch.Domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
