package main.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private Long eventId;
    private Long authorId;
    private CommentStatus status;
    private LocalDateTime created;
    private LocalDateTime updated;
}