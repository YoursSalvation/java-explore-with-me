package main.server.service;

import lombok.RequiredArgsConstructor;
import main.dto.*;
import main.server.exception.ConflictException;
import main.server.exception.NotFoundException;
import main.server.mapper.CommentMapper;
import main.server.model.Comment;
import main.server.model.Event;
import main.server.model.User;
import main.server.repository.CommentRepository;
import main.server.repository.EventRepository;
import main.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot comment unpublished event");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .author(user)
                .event(event)
                .status(CommentStatus.PENDING)
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto dto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Only author can edit comment");
        }

        if (comment.getStatus() == CommentStatus.PUBLISHED) {
            throw new ConflictException("Published comment cannot be edited");
        }

        comment.setText(dto.getText());
        comment.setUpdated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Only author can delete comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventComments(Long eventId) {

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        return commentRepository
                .findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED)
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    // ---------- admin ----------

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getPendingComments() {

        return commentRepository.findAllByStatus(CommentStatus.PENDING)
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto publish(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Only pending comment can be published");
        }

        comment.setStatus(CommentStatus.PUBLISHED);
        comment.setUpdated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto reject(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Only pending comment can be rejected");
        }

        comment.setStatus(CommentStatus.REJECTED);
        comment.setUpdated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteByAdmin(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        commentRepository.delete(comment);
    }
}