package main.server.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.dto.*;
import main.server.client.StatsClient;
import main.server.exception.BadRequestException;
import main.server.exception.ConflictException;
import main.server.exception.NotFoundException;
import main.server.mapper.EventMapper;
import main.server.model.Category;
import main.server.model.Event;
import main.server.model.User;
import main.server.repository.CategoryRepository;
import main.server.repository.EventRepository;
import main.server.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Event event = EventMapper.toEntity(dto);
        event.setInitiator(user);
        event.setCategory(category);

        return EventMapper.toFullDto(eventRepository.save(event), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(e -> EventMapper.toShortDto(e, 0))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        return EventMapper.toFullDto(event, 0);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (dto.getParticipantLimit() != null && dto.getParticipantLimit() < 0) {
            throw new BadRequestException("Participant limit must be positive");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Published event cannot be changed");
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {

                case SEND_TO_REVIEW -> {
                    if (event.getState() != EventState.CANCELED) {
                        throw new ConflictException(
                                "Only canceled event can be sent to review"
                        );
                    }
                    event.setState(EventState.PENDING);
                }

                case CANCEL_REVIEW -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException(
                                "Only pending event can be canceled"
                        );
                    }
                    event.setState(EventState.CANCELED);
                }
            }
        }

        if (dto.getEventDate() != null) {
            LocalDateTime newDate =
                    LocalDateTime.parse(dto.getEventDate(), FORMATTER);

            if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException(
                        "Event date must be at least 2 hours in the future"
                );
            }

            event.setEventDate(newDate);
        }

        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());

        return EventMapper.toFullDto(eventRepository.save(event), 0);
    }

    @Override
    public EventFullDto cancelUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        event.setState(EventState.CANCELED);
        return EventMapper.toFullDto(eventRepository.save(event), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            int from,
            int size
    ) {
        LocalDateTime start = rangeStart == null
                ? LocalDateTime.of(2000, 1, 1, 0, 0)
                : LocalDateTime.parse(rangeStart, FORMATTER);

        LocalDateTime end = rangeEnd == null
                ? LocalDateTime.of(2100, 1, 1, 0, 0)
                : LocalDateTime.parse(rangeEnd, FORMATTER);

        Pageable pageable = PageRequest.of(from / size, size);

        List<EventState> eventStates = states == null ? null :
                states.stream().map(EventState::valueOf).toList();

        Page<Event> page;

        if (users == null && eventStates == null && categories == null) {
            page = eventRepository.findAllByEventDateBetween(start, end, pageable);
        } else {
            page = eventRepository
                    .findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                            users, eventStates, categories, start, end, pageable
                    );
        }

        return page.stream()
                .map(e -> EventMapper.toFullDto(e, 0))
                .toList();
    }

    @Override
    public EventFullDto publish(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Only pending events can be published");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return EventMapper.toFullDto(eventRepository.save(event), 0);
    }

    @Override
    public EventFullDto reject(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot reject published event");
        }

        event.setState(EventState.CANCELED);
        return EventMapper.toFullDto(eventRepository.save(event), 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(
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
    ) {

        Pageable pageable = PageRequest.of(from / size, size);

        LocalDateTime start = rangeStart == null
                ? LocalDateTime.of(2000, 1, 1, 0, 0)
                : LocalDateTime.parse(rangeStart, FORMATTER);

        LocalDateTime end = rangeEnd == null
                ? LocalDateTime.of(2100, 1, 1, 0, 0)
                : LocalDateTime.parse(rangeEnd, FORMATTER);

        if (start.isAfter(end)) {
            throw new BadRequestException(
                    "rangeStart must be before rangeEnd"
            );
        }

        Page<Event> page = eventRepository.findPublicEvents(
                text,
                categories,
                paid,
                start,
                end,
                EventState.PUBLISHED,
                pageable
        );

        Stream<Event> stream = page.stream();

        if (Boolean.TRUE.equals(onlyAvailable)) {
            stream = stream.filter(e ->
                    e.getParticipantLimit() == 0 ||
                            e.getConfirmedRequests() < e.getParticipantLimit()
            );
        }

        List<EventShortDto> result = stream
                .map(e -> EventMapper.toShortDto(e, getViews(e.getId())))
                .toList();

        if (sort == EventSort.EVENT_DATE) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .toList();
        }

        if (sort == EventSort.VIEWS) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .toList();
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {

        Event event = eventRepository.findById(eventId)
                .filter(e -> e.getState() == EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        try {
            statsClient.hit(
                    "ewm-main-service",
                    request.getRequestURI(),
                    request.getRemoteAddr()
            );
        } catch (Exception ignored) {
        }

        long views = 0;

        try {
            List<StatsViewDto> stats = statsClient.getStats(
                    "2000-01-01 00:00:00",
                    "2100-01-01 00:00:00",
                    List.of(request.getRequestURI()),
                    true
            );
            views = stats.isEmpty() ? 0 : stats.get(0).getHits();
        } catch (Exception ignored) {
            views = 0;
        }

        return EventMapper.toFullDto(event, views);
    }

    private long getViews(Long eventId) {
        try {
            String uri = "/events/" + eventId;

            List<StatsViewDto> stats = statsClient.getStats(
                    "2000-01-01 00:00:00",
                    "2100-01-01 00:00:00",
                    List.of(uri),
                    true
            );

            return stats.isEmpty() ? 0 : stats.get(0).getHits();
        } catch (Exception e) {
            return 0;
        }
    }
}