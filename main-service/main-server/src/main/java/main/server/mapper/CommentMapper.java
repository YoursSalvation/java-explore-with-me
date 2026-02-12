package main.server.mapper;

import lombok.experimental.UtilityClass;
import main.dto.CommentDto;
import main.server.model.Comment;

@UtilityClass
public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .authorId(comment.getAuthor().getId())
                .status(comment.getStatus())
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .build();
    }
}