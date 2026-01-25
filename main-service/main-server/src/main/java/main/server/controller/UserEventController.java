package main.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.dto.*;
import main.server.service.EventService;
import main.server.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class UserEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto dto
    ) {
        return eventService.create(userId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody(required = false) @Valid UpdateEventUserRequest dto
    ) {
        return eventService.updateUserEvent(userId, eventId, dto);
    }

    @GetMapping
    public List<EventShortDto> getAll(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return eventService.getUserEventById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/cancel")
    public EventFullDto cancel(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return eventService.cancelUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest dto
    ) {
        return requestService.updateRequestsStatus(userId, eventId, dto);
    }
}