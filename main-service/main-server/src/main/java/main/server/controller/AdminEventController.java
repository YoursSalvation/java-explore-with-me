package main.server.controller;

import lombok.RequiredArgsConstructor;
import main.dto.EventFullDto;
import main.server.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> search(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.searchEvents(
                users, states, categories, rangeStart, rangeEnd, from, size
        );
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publish(@PathVariable Long eventId) {
        return eventService.publish(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto reject(@PathVariable Long eventId) {
        return eventService.reject(eventId);
    }
}