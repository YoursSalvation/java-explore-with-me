package main.server.controller;

import lombok.RequiredArgsConstructor;
import main.dto.ParticipationRequestDto;
import main.server.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class UserRequestController {

    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(
            @PathVariable Long userId,
            @RequestParam Long eventId
    ) {
        return service.create(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAll(
            @PathVariable Long userId
    ) {
        return service.getUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        return service.cancel(userId, requestId);
    }
}