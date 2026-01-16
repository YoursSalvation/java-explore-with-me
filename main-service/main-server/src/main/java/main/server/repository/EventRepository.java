package main.server.repository;

import main.dto.EventState;
import main.server.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // PUBLIC
    @Query("""
        SELECT e FROM Event e
        WHERE e.state = :state
          AND (:text IS NULL OR
               lower(e.annotation) LIKE lower(concat('%', :text, '%')) OR
               lower(e.description) LIKE lower(concat('%', :text, '%')))
          AND (:categories IS NULL OR e.category.id IN :categories)
          AND (:paid IS NULL OR e.paid = :paid)
          AND e.eventDate BETWEEN :start AND :end
        """)
    Page<Event> findPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime start,
            LocalDateTime end,
            EventState state,
            Pageable pageable
    );

    // USER
    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    // ADMIN
    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
            List<Long> initiatorIds,
            List<EventState> states,
            List<Long> categoryIds,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
