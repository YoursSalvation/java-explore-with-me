package main.server.repository;

import main.dto.CommentStatus;
import main.server.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndStatus(
            Long eventId,
            CommentStatus status
    );

    List<Comment> findAllByStatus(CommentStatus status);
}