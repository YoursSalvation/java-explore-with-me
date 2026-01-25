package main.server.controller;

import lombok.RequiredArgsConstructor;
import main.dto.CommentDto;
import main.server.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService service;

    @GetMapping
    public List<CommentDto> getPublished(
            @PathVariable Long eventId
    ) {
        return service.getEventComments(eventId);
    }
}