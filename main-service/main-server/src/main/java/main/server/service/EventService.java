package main.server.service;

import jakarta.servlet.http.HttpServletRequest;
import main.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    /* ================= USER ================= */

    EventFullDto create(Long userId, NewEventDto dto);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventFullDto cancelUserEvent(Long userId, Long eventId);

    /* ================= ADMIN ================= */

    List<EventFullDto> searchEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    EventFullDto publish(Long eventId);

    EventFullDto reject(Long eventId);

    /* ================= PUBLIC ================= */

    List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            EventSort sort,
            int from,
            int size,
            HttpServletRequest request
    );

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);
}
