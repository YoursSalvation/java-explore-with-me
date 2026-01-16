package main.server.repository;

import main.server.model.RequestStatus;
import main.server.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}