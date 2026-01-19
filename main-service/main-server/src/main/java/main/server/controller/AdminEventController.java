package main.server.controller;

import lombok.RequiredArgsConstructor;
import main.dto.EventFullDto;
import main.dto.UpdateEventAdminRequest;
import main.server.service.EventService;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.searchEvents(
                users, states, categories, rangeStart, rangeEnd, from, size
        );
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long eventId,
            @RequestBody UpdateEventAdminRequest dto
    ) {
        if ("PUBLISH_EVENT".equals(dto.getStateAction())) {
            return eventService.publish(eventId);
        }
        if ("REJECT_EVENT".equals(dto.getStateAction())) {
            return eventService.reject(eventId);
        }
        return eventService.updateAdminEvent(eventId, dto);
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