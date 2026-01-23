package main.server.repository;

import main.dto.RequestStatus;
import main.server.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
                select r from Request r
                where r.requester.id = :userId
                  and r.event.initiator.id <> :userId
                order by r.created asc
            """)
    List<Request> findUserRequests(Long userId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}