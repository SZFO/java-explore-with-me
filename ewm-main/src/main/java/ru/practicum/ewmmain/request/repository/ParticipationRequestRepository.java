package ru.practicum.ewmmain.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmain.request.model.ParticipationRequest;
import ru.practicum.ewmmain.request.model.StateParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByRequesterId(Long userId);

    @Query("select count(p) from ParticipationRequest p where p.event.id IN ?1 and p.status = ?2")
    Long countAllByEventIdAndStatus(List<Long> eventIds, StateParticipationRequest stateParticipationRequest);
}