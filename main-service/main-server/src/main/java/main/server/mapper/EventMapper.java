package main.server.mapper;

import lombok.experimental.UtilityClass;
import main.dto.*;
import main.server.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /* =========================
       CREATE
       ========================= */
    public Event toEntity(NewEventDto dto) {
        return Event.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(LocalDateTime.parse(dto.getEventDate(), DATE_TIME_FORMATTER))
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .build();
    }

    /* =========================
       UPDATE (USER)
       ========================= */
    public void updateFromUserRequest(Event event, UpdateEventUserRequest dto) {
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(
                    LocalDateTime.parse(dto.getEventDate(), DATE_TIME_FORMATTER)
            );
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
    }

    /* =========================
       SHORT
       ========================= */
    public EventShortDto toShortDto(Event event, long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .views(views)
                .build();
    }

    /* =========================
       FULL
       ========================= */
    public EventFullDto toFullDto(Event event, long views) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toDto(event.getCategory()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .views(views)
                .build();
    }
}