package ru.practicum.ewmmain.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmmain.request.model.ParticipationRequest;
import ru.practicum.ewmmain.request.model.StateParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByRequesterId(Long userId);

    Long countAllByEventIdAndStatus(Long eventId, StateParticipationRequest stateParticipationRequest);
}