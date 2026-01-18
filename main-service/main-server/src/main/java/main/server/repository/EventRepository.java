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

    @Query("""
            select e from Event e
            where e.state = :state
            and (:text is null or lower(e.description) like lower(concat('%', :text, '%')))
            and (:categories is null or e.category.id in :categories)
            and (:paid is null or e.paid = :paid)
            and e.eventDate between :start and :end
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

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
            List<Long> initiatorIds,
            List<EventState> states,
            List<Long> categoryIds,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    @Query("""
                select e from Event e
                where e.eventDate between :start and :end
            """)
    Page<Event> findAllByEventDateBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
