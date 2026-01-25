package main.server.controller;

import lombok.RequiredArgsConstructor;
import main.dto.CommentDto;
import main.server.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService service;

    @GetMapping("/pending")
    public List<CommentDto> getPending() {
        return service.getPendingComments();
    }

    @PatchMapping("/{commentId}/approve")
    public CommentDto approve(@PathVariable Long commentId) {
        return service.publish(commentId);
    }

    @PatchMapping("/{commentId}/reject")
    public CommentDto reject(@PathVariable Long commentId) {
        return service.reject(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        service.deleteByAdmin(commentId);
    }
}
