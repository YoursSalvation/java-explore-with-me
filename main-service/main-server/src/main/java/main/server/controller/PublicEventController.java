package main.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.dto.EventFullDto;
import main.dto.EventShortDto;
import main.dto.EventSort;
import main.server.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        return eventService.getPublicEvents(
                text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request
        );
    }

    @GetMapping("/{id}")
    public EventFullDto getById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        return eventService.getPublicEventById(id, request);
    }
}
