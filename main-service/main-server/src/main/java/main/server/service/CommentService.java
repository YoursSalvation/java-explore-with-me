package main.server.service;

import main.dto.CommentDto;
import main.dto.NewCommentDto;
import main.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto dto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto dto);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getEventComments(Long eventId);

    List<CommentDto> getPendingComments();

    CommentDto publish(Long commentId);

    CommentDto reject(Long commentId);

    void deleteByAdmin(Long commentId);
}