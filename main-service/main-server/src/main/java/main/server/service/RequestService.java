package main.server.service;

import main.dto.EventRequestStatusUpdateRequest;
import main.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    List<ParticipationRequestDto> updateRequestsStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest dto
    );

}