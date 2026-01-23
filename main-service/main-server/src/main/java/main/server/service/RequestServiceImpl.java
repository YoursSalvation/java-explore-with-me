package main.server.service;

import lombok.RequiredArgsConstructor;
import main.dto.EventRequestStatusUpdateRequest;
import main.dto.EventState;
import main.dto.ParticipationRequestDto;
import main.server.exception.ConflictException;
import main.server.exception.NotFoundException;
import main.server.mapper.RequestMapper;
import main.server.model.Event;
import main.server.model.Request;
import main.dto.RequestStatus;
import main.server.model.User;
import main.server.repository.EventRepository;
import main.server.repository.RequestRepository;
import main.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event must be published");
        }

        if (event.getParticipantLimit() > 0) {
            long confirmed = requestRepository
                    .countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

            if (confirmed >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached");
            }
        }

        RequestStatus status =
                (!event.getRequestModeration() || event.getParticipantLimit() == 0)
                        ? RequestStatus.CONFIRMED
                        : RequestStatus.PENDING;

        Request request = Request.builder()
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .event(event)
                .requester(user)
                .status(status)
                .build();

        Request saved = requestRepository.save(request);

        if (status == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        return RequestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> updateRequestsStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest dto
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only initiator can change request status");
        }

        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());

        if (requests.isEmpty()) {
            throw new NotFoundException("Requests not found");
        }

        if (dto.getStatus() == RequestStatus.CONFIRMED) {

            for (Request r : requests) {
                if (r.getStatus() != RequestStatus.PENDING) {
                    throw new ConflictException(
                            "Only pending requests can be confirmed"
                    );
                }
            }

            long confirmed = requestRepository
                    .countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

            if (event.getParticipantLimit() > 0 &&
                    confirmed + requests.size() > event.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached");
            }

            for (Request r : requests) {
                r.setStatus(RequestStatus.CONFIRMED);
            }

            event.setConfirmedRequests(
                    event.getConfirmedRequests() + requests.size()
            );

        } else if (dto.getStatus() == RequestStatus.REJECTED) {

            for (Request r : requests) {
                if (r.getStatus() != RequestStatus.PENDING) {
                    throw new ConflictException(
                            "Only pending requests can be rejected"
                    );
                }
                r.setStatus(RequestStatus.REJECTED);
            }
        }

        requestRepository.saveAll(requests);

        return requests.stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findUserRequests(userId).stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only initiator can view event requests");
        }

        return requestRepository.findByEventId(eventId).stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Only requester can cancel request");
        }

        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }
}