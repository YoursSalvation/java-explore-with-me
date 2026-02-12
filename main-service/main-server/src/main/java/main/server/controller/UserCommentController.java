package main.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.dto.CommentDto;
import main.dto.NewCommentDto;
import main.dto.UpdateCommentDto;
import main.server.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
public class UserCommentController {

    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid NewCommentDto dto
    ) {
        return service.addComment(userId, eventId, dto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentDto dto
    ) {
        return service.updateComment(userId, commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        service.deleteComment(userId, commentId);
    }
}